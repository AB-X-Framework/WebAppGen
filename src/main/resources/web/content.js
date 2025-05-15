
class PageContent {

    static renderPage(name){
        $.get(`/page/specs/${name}`,(content)=>{
            $("#body-content").html(content);
        })
    }
}