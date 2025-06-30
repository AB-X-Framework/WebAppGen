var page;
var size;

function userList() {
    function createChild(curr, key, value, id) {

        const delId= `d_${id}`;
        const escapedKey = JSON.stringify(key);
        const textfield= {
            "specs": {"title": `${curr}`, "content": value},
            js: `$(${id}).on('input',()=>{workingEnv.SaveArray.markChanged();workingEnv.updatedArray[(${escapedKey})]=$(${id}).val()})`,
            "isContainer": false,
            "type": "textarea",
            "size": "l11",
            "id": id
        }
        const deleteBtn={
            "specs": {"title": "delete"},
            js: `$(${delId}).click(()=>{handleDelete(${escapedKey})})`,
            "isContainer": false,
            "type": "button",
            "size": "l1",
            "id": delId
        }
        return {
            "isContainer": true,
            "js": "",
            "id": "__array",
            "layout": "horizontal",
            "children": [textfield,deleteBtn]
        }
    }

    const children = [];
    const entries = resourceModel.getUsers(page, size);
    const len = entries.length();
    let curr = page*size+1;
    for (let i = 0; i < len; ++i) {
        const elem = entries.get(i);
        const key = elem.get("name");
        const value = elem.get("role");
        const id = `array_${parseInt(Math.random() * 1000000)}`;
        children.push(createChild(curr,key, value, id));
        ++curr;
    }
    return JSON.stringify({
        "isContainer": true,
        "js": "",
        "id": "__array",
        "layout": "container",
        "children": children
    });
}