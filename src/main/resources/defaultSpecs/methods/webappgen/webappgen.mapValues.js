var map;
var page;
var size;

function mapValues() {
    const children = [];
    const entries = resourceModel.getMapEntries(map, page, size);
    const len = entries.length();
    for (let i = 0; i< len;++i){
        const elem = entries.get(i);
        const key = elem.get("key");
        const scapedKey = JSON.stringify(elem.get("key"));
        const value = elem.get("value");
        const id = `map_${parseInt(Math.random()*1000000) }`
        children.push({
            "specs":{"title":key,"content":value},
            js:`$(${id}).on('input',()=>{workingEnv.SaveMap.markChanged();workingEnv.updatedMap[(${scapedKey})]=$(${id}).val()})`,
            "isContainer":false,
            "type":"textarea",
            "size":"l12",
            "id":id
        });
    }
    return JSON.stringify({
        "isContainer": true,
        "js": "",
        "id": "__map",
        "layout": "container",
        "children":children
    });
}