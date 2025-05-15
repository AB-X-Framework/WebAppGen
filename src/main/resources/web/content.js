
class PageContent {

    static processTile(specs){
        document.title = specs.title;
    }
    static renderPage(name){
        $.get(`/page/specs/${name}`,(specs)=>{
            PageContent.processTile(specs);
            $("#body-content").html(specs);
        })
    }
}