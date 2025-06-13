function handleDelete(key){
    key = eval(`(${key})`);
    $(workingEnv.DeletePopup.ok).click(()=>{
        $.get(`/resources/maps/delete/${workingEnv.mapName}/${key}`,(status)=>{
            if (status.success){
                PageContent.closeModal(workingEnv.DeletePopup);
                workingEnv.renderMap();
            }else {
                PageContent.showModal(`Cannot delete ${key}. ${JSON.stringify(success.error)}`)
            }
        })
    });
    PageContent.openModal(workingEnv.DeletePopup);
}