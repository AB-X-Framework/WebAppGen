var map;
var page;
var size;

function mapValues() {
    const children = [];
    const entries = resourceModel.getMapEntries(map, page, size);
    const len = entries.length();
    for (let i = 0; i< len;++i){
        const elem = entries.get(i);
        children.push({
            "specs":{"title":elem.get("key"),"content":elem.get("value")},
            js:"",
            "isContainer":false,
            "type":"textfield",
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