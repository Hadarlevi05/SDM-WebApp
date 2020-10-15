dataType: "json",
    function $post(url, data) {
        return $ajax('POST', url, data);
    }

function $get(url, data) {
    return $ajax('GET', url, data);
}

function init() {
    $.ajaxSetup({
        dataType: "json",
        global: false,
        type: "POST"
    });
}

function $ajax(verb, url, data) {
    return $.ajax(url, {
        type: 'GET',  // http method
        data: data,
        error: function (jqXhr, textStatus, errorMessage) {
            console.log('error!', textStatus, errorMessage);
        }
    });
}

init();