var map;
var page;
var size;

function mapValues() {
    return JSON.stringify({
        "specs": {},
        "package": "webappgen.base",
        "name": "webappgen.base.divider",
        "isContainer": false,
        "js": "",
        "id": "__preview",
        "type": "divider",
        "Ddata":resourceModel.getMapEntries(map,page,size)
    });
}