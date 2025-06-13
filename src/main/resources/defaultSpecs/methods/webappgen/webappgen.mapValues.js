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
        const value = elem.get("value");
        children.push({
            "specs":{"title":key,"content":value},
            js:"$(self).on('input',()=>{workingEnv.SaveMap.markChanged();})",
            "isContainer":false,
            "type":"textarea",
            "size":"l12",
            "id":`map_${elem.id}`
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