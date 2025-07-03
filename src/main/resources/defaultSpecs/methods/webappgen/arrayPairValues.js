var arrayPair;
var page;
var size;

function arrayPairValues() {
    function createChild(curr, id, key, value) {

        const delId= `d_${id}`;
        const keyId= `k_${id}`;
        const valueId= `v_${id}`;
        const escapedKey = JSON.stringify(key);
        const escapedValue = JSON.stringify(value);
        const keyfield= {
            "specs": {"title": `${curr} Key`, "content": key},
            "js": `workingEnv.updatedArrayPair[${id}] = {"key":(${escapedKey}),"value":(${escapedValue})};
                $(${keyId}).on('input',()=>{workingEnv.SaveArrayPair.markChanged();workingEnv.updatedArrayPair[${id}].key=$(${keyId}).val()})`,
            "isContainer": false,
            "type": "textfield",
            "size": "l4",
            "id": keyId
        }
        const valuefield= {
            "specs": {"title": `Value`, "content": value},
            "js": `$(${valueId}).on('input',()=>{workingEnv.SaveArrayPair.markChanged();workingEnv.updatedArrayPair[${id}].value=$(${valueId}).val()})`,
            "isContainer": false,
            "type": "textarea",
            "size": "l5",
            "id": valueId
        }
        const deleteBtn={
            "specs": {"title": "delete"},
            js: `$(${delId}).click(()=>{handleDelete(${id})})`,
            "isContainer": false,
            "type": "button",
            "size": "l1",
            "id": delId
        }
        return {
            "isContainer": true,
            "js": "",
            "id": "__arrayPair",
            "layout": "horizontal",
            "children": [keyfield,valuefield,deleteBtn]
        }
    }

    const children = [];
    const entries = resourceModel.getArrayPairEntries(arrayPair, page, size);
    const len = entries.length();
    let curr = page*size+1;
    for (let i = 0; i < len; ++i) {
        const elem = entries.get(i);
        const id = elem.get("id");
        const key = elem.get("key");
        const value = elem.get("value");
        children.push(createChild(curr,id,key, value));
        ++curr;
    }
    return JSON.stringify({
        "isContainer": true,
        "js": "",
        "id": "__arrayPair",
        "layout": "container",
        "children": children
    });
}