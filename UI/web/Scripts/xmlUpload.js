function loadFile(e) {
    var file = e.target.files[0];
    var reader = new FileReader();

    reader.onload = function (){
        var content = reader.result;

        $.ajax({
            url: "../../LoadSdm",
            data: { file: content },
            type: 'POST',
            cache: false,

            success: function(finalMsg) {
                updateRepoAfterLoadFile();
                console.log(finalMsg);
            }
        });
    };
    reader.readAsText(file);
}

function updateRepoAfterLoadFile() {
    // ajaxCurrentUserRepo();
}