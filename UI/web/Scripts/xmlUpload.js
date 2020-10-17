function loadFile(e, callback) {
    var file = e.target.files[0];
    var reader = new FileReader();

    reader.onload = function (){
        var content = reader.result;

        $.ajax({
            url: "../../LoadSdm",
            data: { file: content },
            type: 'POST',
            cache: false,
            success: function(data) {
                callback(data);
            }
        });
    };
    reader.readAsText(file);
}