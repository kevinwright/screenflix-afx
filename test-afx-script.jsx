//JSON Shim
"object"!=typeof JSON&&(JSON={}),function(){"use strict";var rx_one=/^[\],:{}\s]*$/,rx_two=/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,rx_three=/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,rx_four=/(?:^|:|,)(?:\s*\[)+/g,rx_escapable=/[\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,rx_dangerous=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta,rep;function f(t){return t<10?"0"+t:t}function this_value(){return this.valueOf()}function quote(t){return rx_escapable.lastIndex=0,rx_escapable.test(t)?'"'+t.replace(rx_escapable,function(t){var e=meta[t];return"string"==typeof e?e:"\\u"+("0000"+t.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+t+'"'}function str(t,e){var r,n,o,u,f,a=gap,i=e[t];switch(i&&"object"==typeof i&&"function"==typeof i.toJSON&&(i=i.toJSON(t)),"function"==typeof rep&&(i=rep.call(e,t,i)),typeof i){case"string":return quote(i);case"number":return isFinite(i)?String(i):"null";case"boolean":case"null":return String(i);case"object":if(!i)return"null";if(gap+=indent,f=[],"[object Array]"===Object.prototype.toString.apply(i)){for(u=i.length,r=0;r<u;r+=1)f[r]=str(r,i)||"null";return o=0===f.length?"[]":gap?"[\n"+gap+f.join(",\n"+gap)+"\n"+a+"]":"["+f.join(",")+"]",gap=a,o}if(rep&&"object"==typeof rep)for(u=rep.length,r=0;r<u;r+=1)"string"==typeof rep[r]&&(o=str(n=rep[r],i))&&f.push(quote(n)+(gap?": ":":")+o);else for(n in i)Object.prototype.hasOwnProperty.call(i,n)&&(o=str(n,i))&&f.push(quote(n)+(gap?": ":":")+o);return o=0===f.length?"{}":gap?"{\n"+gap+f.join(",\n"+gap)+"\n"+a+"}":"{"+f.join(",")+"}",gap=a,o}}"function"!=typeof Date.prototype.toJSON&&(Date.prototype.toJSON=function(){return isFinite(this.valueOf())?this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z":null},Boolean.prototype.toJSON=this_value,Number.prototype.toJSON=this_value,String.prototype.toJSON=this_value),"function"!=typeof JSON.stringify&&(meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},JSON.stringify=function(t,e,r){var n;if(indent=gap="","number"==typeof r)for(n=0;n<r;n+=1)indent+=" ";else"string"==typeof r&&(indent=r);if((rep=e)&&"function"!=typeof e&&("object"!=typeof e||"number"!=typeof e.length))throw new Error("JSON.stringify");return str("",{"":t})}),"function"!=typeof JSON.parse&&(JSON.parse=function(text,reviver){var j;function walk(t,e){var r,n,o=t[e];if(o&&"object"==typeof o)for(r in o)Object.prototype.hasOwnProperty.call(o,r)&&(void 0!==(n=walk(o,r))?o[r]=n:delete o[r]);return reviver.call(t,e,o)}if(text=String(text),rx_dangerous.lastIndex=0,rx_dangerous.test(text)&&(text=text.replace(rx_dangerous,function(t){return"\\u"+("0000"+t.charCodeAt(0).toString(16)).slice(-4)})),rx_one.test(text.replace(rx_two,"@").replace(rx_three,"]").replace(rx_four,"")))return j=eval("("+text+")"),"function"==typeof reviver?walk({"":j},""):j;throw new SyntaxError("JSON.parse")})}();
//Array.map Shim
Array.prototype.map||(Array.prototype.map=function(r,t){var n,o,e;if(null==this)throw new TypeError(" this is null or not defined");var i=Object(this),a=i.length>>>0;if("function"!=typeof r)throw new TypeError(r+" is not a function");for(t&&(n=t),o=new Array(a),e=0;e<a;){var p,f;e in i&&(p=i[e],f=r.call(n,p,e,i),o[e]=f),e++}return o});

// Store the export directory
const exportDir = "/Users/kevin/Library/Application Support/com.araeliumgroup.screenflick/Movies/Screenflick Movie extract/";


// Main execution
(function main() {
    const activeItem = app.project.activeItem;
    if (activeItem == null || !(activeItem instanceof CompItem)) {
        alert("You need to select a comp first.");
    } else {
        alert("working in " + activeItem.name);

        try {
            loadAndProcessSummaryFile(exportDir + "summary.json");
        } catch (e) {
            alert("Error: " + e.toString());
        }
    }
})();

function loadAndProcessSummaryFile(fileName) {
    const theFile = new File(fileName);

    alert("Summary file is: " + fileName);

    if (!theFile.exists) {
        alert("Summary file does not exist at: " + fileName);
        return;
    }

    if (theFile.open("r")) {
        theFile.encoding = "UTF-8";
        const jsonStr = theFile.read();
        theFile.close();

        try {
            const summary = JSON.parse(jsonStr);
            processSummary(summary);
        } catch (e) {
            alert("Error parsing JSON: " + e.toString());
        }
    } else {
        alert("Could not open summary file!");
    }
}

function newCursorImageFootage(summary) {
    if (!summary || !summary.images || !summary.images.length) {
        alert("No cursor images found in summary file!");
        return null;
    }

    try {
        const seqStartFile = new File(exportDir + summary.images[0].filename);

        if (!seqStartFile.exists) {
            alert("Cursor image file does not exist at: " + seqStartFile.fsName);
            return null;
        }

        const importOptions = new ImportOptions(seqStartFile);
        importOptions.importAs = ImportAsType.FOOTAGE;
        importOptions.sequence = true;

        const cursorImageFootage = app.project.importFile(importOptions);
        cursorImageFootage.name = "cursor-image-frames";
        return cursorImageFootage;
    } catch (e) {
        alert("Error importing cursor images: " + e.toString());
        return null;
    }
}

function processSummary(summary) {
    if (!summary) {
        alert("No summary data available!");
        return;
    }

    try {
        // First check if cursor-image-frames already exists
        let cursorImageFootage = itemNamed("cursor-image-frames");

        // If not, create it
        if (!cursorImageFootage) {
            cursorImageFootage = newCursorImageFootage(summary);

            if (!cursorImageFootage) {
                alert("Failed to create cursor image footage!");
                return;
            }
        }

        const activeItem = app.project.activeItem;

        // Add cursor image layer
        const cursorImageLayer = activeItem.layers.add(cursorImageFootage, activeItem.duration);
        cursorImageLayer.moveToBeginning();
        cursorImageLayer.timeRemapEnabled = true;
        cursorImageLayer.outPoint = activeItem.duration;
        cursorImageLayer.scale.setValue([10, 10]);
        cursorImageLayer.anchorPoint.setValue([250, 250]);

        // Check for click effect footage
        const cursorClickFootage = itemNamed("click-effect");
        if (!cursorClickFootage) {
            alert("Click effect footage not found! Please import a click effect and name it 'click-effect'.");
            return;
        }

        // Add click effect layer
        const cursorClickLayer = activeItem.layers.add(cursorClickFootage, activeItem.duration);
        cursorClickLayer.moveToBeginning();
        cursorClickLayer.timeRemapEnabled = true;
        cursorClickLayer.outPoint = activeItem.duration;

        // Assign motion and clicks
        if (summary.mouseMotion) {
            assignMouseMotion(cursorImageLayer, summary.mouseMotion);
        } else {
            alert("No mouse motion data found in summary!");
        }

        if (summary.mouseEvents) {
            assignMouseClicks(cursorClickLayer, summary.mouseEvents);
        } else {
            alert("No mouse events data found in summary!");
        }

        alert("Script completed successfully!");

    } catch (e) {
        alert("Error in processSummary: " + e.toString());
    }
}

function assignMouseMotion(layer, keyframes) {
    if (!keyframes || keyframes.length === 0) {
        alert("No mouse motion keyframes to process!");
        return;
    }

    try {
        const frameDuration = layer.source.frameDuration;
        let prevId = -1;
        let prevPos;

        const frameTimes = [];
        const framePositions = [];

        const timeProp = layer.property("timeRemap");

        keyframes.map(function(kf) {
            const t = kf.when.seconds;
            const newPos = [kf.x, kf.y];

            if (!prevPos || newPos[0] !== prevPos[0] || newPos[1] !== prevPos[1]) {
                frameTimes.push(t);
                framePositions.push(newPos);
                prevPos = newPos;
            }

            if (kf.imageId !== prevId) {
                const id = kf.imageId;
                layer.Marker.setValueAtTime(t, new MarkerValue(id.toString()));
                const keyIdx = timeProp.addKey(t);
                timeProp.setValueAtKey(keyIdx, id * frameDuration);
                timeProp.setInterpolationTypeAtKey(keyIdx, KeyframeInterpolationType.HOLD);
                prevId = id;
            }
        });

        layer.position.setValuesAtTimes(frameTimes, framePositions);
    } catch (e) {
        alert("Error in assignMouseMotion: " + e.toString());
    }
}

function assignMouseClicks(layer, keyframes) {
    if (!keyframes || keyframes.length === 0) {
        alert("No mouse click keyframes to process!");
        return;
    }

    try {
        const timeProp = layer.property("timeRemap");
        const aniDuration = layer.source.duration;
        const frameDuration = layer.containingComp.frameDuration;

        keyframes.map(function(kf) {
            if (kf.eventType === "leftMouseDown") {
                const t = kf.when.seconds;
                layer.Marker.setValueAtTime(t, new MarkerValue("click"));
                //const startKeyIdx = timeProp.addKey(t);
                timeProp.setValueAtTime(t, 0);
                timeProp.setValueAtTime(t + aniDuration, aniDuration);
                timeProp.setValueAtTime(t + aniDuration + frameDuration, 0);
            }
        });
    } catch (e) {
        alert("Error in assignMouseClicks: " + e.toString());
    }
}

function itemNamed(name) {
    for (let i = 1; i <= app.project.numItems; i++) {
        if (app.project.item(i).name === name) {
            return app.project.item(i);
        }
    }
    return null; // Explicitly return null if not found
}