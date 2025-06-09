var modelPath;
var removeDefaults

function saveModel() {
    let result = {}
    try {
        result.success = true;
        specsExporter.createSpecs(modelPath, removeDefaults);
    }catch (e){
        result.success = false;
        result.error = JSON.stringify(e);
    }
    return JSON.stringify(result);
}