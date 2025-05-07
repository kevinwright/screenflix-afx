import bpy
import os
import json

from bpy.props import (IntProperty, StringProperty, PointerProperty)
from bpy.types import (Panel, Operator, PropertyGroup, Scene)
from bpy.utils import (register_class, unregister_class)
from pathlib import Path

scriptdir = Path(__file__).resolve().parent
print(scriptdir)

class ScreenflickImportProperties(PropertyGroup):

    someInt: IntProperty(
        name="Int Property",
        description="Just a number",
    )
    
    sflix_video_file: StringProperty(
        name='Video File',
        description='The video rendered by ScreenFlix, with no pointer or key events',
        subtype='FILE_PATH'
    )
    
    sflix_summary_file: StringProperty(
        name='Summary File',
        description='The generated summary.json file',
        subtype='FILE_PATH'
    )
    
class SEQUENCE_OT_ScreenflixImportOperator(Operator):
    bl_idname = "sflix.import"
    bl_label = "Screenflix Import"
    bl_options = {'REGISTER'}
    
    def execute(self, context):
        scene = context.scene
        
        video_file = context.scene.screenflick.sflix_video_file        
        summary_file = context.scene.screenflick.sflix_summary_file
        summary_dir = os.path.dirname(summary_file)
        
        #load clip, set scene to same framerate
        bpy.ops.sequencer.movie_strip_add(
            filepath=video_file,
            frame_start=1,
            channel=1,
            fit_method='ORIGINAL',
            use_framerate=True
        )
        
        movie_strip = bpy.context.active_sequence_strip
        movie_strip.name = os.path.basename(video_file)
        
        #set end frame and output dimensions to match clip
        res_x = movie_strip.elements[0].orig_width
        res_y = movie_strip.elements[0].orig_height
        
        scene.frame_end = movie_strip.frame_final_duration
        scene.render.resolution_percentage = 100
        scene.render.resolution_x = res_x
        scene.render.resolution_y = res_y

        with open(summary_file) as json_data:
            summary = json.load(json_data)
            
            imgs = {}
            for img_entry in summary["images"]:
                img_id = img_entry["id"]
                img_filename = img_entry["filename"]
                imgs[img_id] = img_filename
                
            for img_filename in imgs.values():    
                bpy.ops.sequencer.image_strip_add(
                    directory=summary_dir,
                    files=[{"name": img_filename}],
                    frame_start=1,
                    frame_end=scene.frame_end,
                    fit_method='ORIGINAL',
                )
                strip = bpy.context.active_sequence_strip
                strip.name = img_filename
                
            for img_filename in imgs.values(): 
                bpy.context.scene.sequence_editor.strips_all[img_filename].select = True

            bpy.ops.sequencer.meta_make()
    
            mouse_positions = {}

            for mm in summary["mouseMotion"]:
                frame = mm["when"]["frames"]
                img_id = mm["imageId"]
                #scale and re-centre coords
                x = 2 * mm["x"] - res_x/2
                y = 2 * mm["y"] - res_y/2
                
                mouse_positions[frame] = {"x": x, "y": y}

                for id in imgs.keys(): 
                    strip = bpy.context.scene.sequence_editor.strips_all[imgs[id]]
                    strip.blend_alpha = 1.0 if id == img_id else 0.0
                    strip.keyframe_insert("blend_alpha", frame=frame, keytype='KEYFRAME')
                
                meta_strip = bpy.context.scene.sequence_editor.strips_all["MetaStrip"]
                meta_strip.transform.scale_x = 0.3
                meta_strip.transform.scale_y = 0.3
                meta_strip.transform.offset_x = x
                meta_strip.transform.offset_y = y
                meta_strip.transform.keyframe_insert("offset_x", frame=frame, keytype='KEYFRAME')
                meta_strip.transform.keyframe_insert("offset_y", frame=frame, keytype='KEYFRAME')

            bpy.ops.sequencer.select_all(action='SELECT')
            # movie_strip.select = True
            # meta_strip.select = True
            bpy.ops.sequencer.connect()
            bpy.ops.sequencer.select_all(action='DESELECT')

            clickfile = Path.joinpath(scriptdir, "click-effect.webm").as_uri()
            init_click_strip = None
            init_click_strip_frame = 0

            for evt in summary["mouseEvents"]:
                frame = evt["when"]["frames"]
                #scale and re-centre coords
                x = mouse_positions[frame]["x"]
                y = mouse_positions[frame]["y"]

                if evt["eventType"].endswith("MouseDown"):
                    self.report({'INFO'}, f"click at frame ${frame}")

                    if init_click_strip is None:
                        self.report({'INFO'}, "initial")

                        bpy.ops.sequencer.movie_strip_add(
                            filepath=clickfile,
                            frame_start=frame,
                            fit_method='ORIGINAL',
                            use_framerate=False
                        )
                        
                        init_click_strip = bpy.context.active_sequence_strip
                        init_click_strip.blend_type = "DIFFERENCE"
                        init_click_strip.transform.scale_x = 0.1
                        init_click_strip.transform.scale_y = 0.1
                        init_click_strip.transform.offset_x = x
                        init_click_strip.transform.offset_y = y
                        init_click_strip_frame = frame
                    else:    
                        self.report({'INFO'}, "duplicating")

                        bpy.ops.sequencer.select_all(action='DESELECT')
                        init_click_strip.select = True

                        bpy.ops.sequencer.duplicate()
                        click_strip = [strip for strip in bpy.context.strips if strip.name.startswith("click-effect")][-1]
                        print(f"click_strip: ${click_strip}")
                        click_strip.frame_start = frame
                        click_strip.transform.offset_x = x
                        click_strip.transform.offset_y = y
        
        stripnames = [strip.name for strip in bpy.context.strips if strip.name.startswith("click-effect")]
        for name in stripnames:
            print(name)

        return {'FINISHED'}

    
class SEQUENCE_PT_Screenflix(Panel):
    """Creates a Panel in the Sequence Editor UI"""
    bl_label = "Screenflix import Panel"
    bl_idname = "SEQUENCE_PT_Screenflix"
    
    bl_space_type = 'SEQUENCE_EDITOR'
    bl_region_type = 'UI'
    bl_label = "Screenflix"
    bl_category = "Screenflix"

    def draw(self, context):
        layout = self.layout
        obj = context.object
        scene = context.scene

        col = layout.column(align=True)
        col.prop(scene.screenflick, "sflix_video_file", expand=True )
        col.prop(scene.screenflick, "sflix_summary_file", expand=True )
        op = col.operator("sflix.import")

classes = (
    ScreenflickImportProperties,
    SEQUENCE_OT_ScreenflixImportOperator,
    SEQUENCE_PT_Screenflix
)

def register():
    print("Registering classes")
    for cls in classes:
        register_class(cls)
    
    Scene.screenflick = bpy.props.PointerProperty(type=ScreenflickImportProperties)


def unregister():
    print("Unregistering classes")
    for cls in reversed(classes):
        unregister_class(cls)

    del Scene.screenflick

print(f"Script is running with name ${__name__}")

if __name__ == "__main__":
    try: unregister()
    except: pass
    register()
