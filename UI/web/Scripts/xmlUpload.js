function loadFile(e, callback) {
    var file = e.target.files[0];
    var reader = new FileReader();

    reader.onload = function () {
        var content = reader.result;

        const postData = {file: content};

        $post("../../LoadSdm", postData).then((data) => {
            callback(data);
        });
    };
    reader.readAsText(file);
}