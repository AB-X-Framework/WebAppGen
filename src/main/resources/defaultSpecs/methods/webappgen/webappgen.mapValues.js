var map;
var page;
var size;

function mapValues() {
    function createChild(key, value, id) {
        const delId= `d_{id}`;
        const escapedKey = JSON.stringify(key);
        const textfield= {
            "specs": {"title": key, "content": value},
            js: `$(${id}).on('input',()=>{workingEnv.SaveMap.markChanged();workingEnv.updatedMap[(${escapedKey})]=$(${id}).val()})`,
            "isContainer": false,
            "type": "textarea",
            "size": "l11",
            "id": id
        }
        const deleteBtn={
            "specs": {"title": "delete"},
            js: `$(${delId}).click(()=>{})`,
            "isContainer": false,
            "type": "button",
            "size": "l1",
            "id": delId
        }
        return {
            "isContainer": true,
            "js": "",
            "id": "__map",
            "layout": "horizontal",
            "children": [textfield,deleteBtn]
        }
    }

    const children = [];
    const entries = resourceModel.getMapEntries(map, page, size);
    const len = entries.length();
    for (let i = 0; i < len; ++i) {
        const elem = entries.get(i);
        const key = elem.get("key");
        const value = elem.get("value");
        const id = `map_${parseInt(Math.random() * 1000000)}`;
        children.push(createChild(key, value, id));
    }
    return JSON.stringify({
        "isContainer": true,
        "js": "",
        "id": "__map",
        "layout": "container",
        "children": children
    });
}