let currentUserSession = null;

const OrderStatusEnum = {
    'NEW': 'NEW',
    'IN_PROGRESS': 'IN_PROGRESS',
    'DONE': 'DONE'
}

function $post(url, data, loader = true) {
    return $ajax('POST', url, data, loader);
}

function $get(url, data, loader = true) {
    return $ajax('GET', url, data, loader);
}

function $ajax(verb, url, data, loader= true) {
    showLoader(loader);

    return $.ajax(url, {
        type: verb,
        data: data,
        cache: false,
        error: function (jqXhr, textStatus, errorMessage) {

            console.log('error', textStatus, errorMessage);
            console.log('error', data.ErrorMessage);
        },
        complete: () => {
            showLoader(false);
        }
    });
}

function redirectUrl(url) {
    location.href = url;
}

function redirectToLogin(url) {
    redirectUrl(`${location.origin}/UI_Web_exploded/Pages/login/login.html`);
}

function init() {
    $.ajaxSetup({
        dataType: "json",
        // contentType: 'application/json; charset=utf-8',
        global: false,
        type: "POST"
    });

    currentUserSession = userSession();
}

function setPermission() {
    if (!currentUserSession) {
        redirectToLogin();
    }

    if (currentUserSession.userType === 'CUSTOMER') {
        $('[role=store-owner-permission]').hide();
    }

    if (currentUserSession.userType === 'STORE_OWNER') {
        $('[role=customer-permission]').hide();
    }
}

function showLoader(flag) {
    if (flag) {
        $('.loading').show();
    } else {
        setTimeout(() => {
            $('.loading').hide();
        }, 200);
    }
}

function userSession(data) {
    if (data) {
        localStorage.setItem('userSession', JSON.stringify(data));
    } else {
        const storageData = localStorage.getItem('userSession');
        if (storageData) {
            return JSON.parse(storageData);
        }
    }
    return null;
}

init();

function setCurrentUser(user) {
    $('#userName').html(user.username);
}

function showToaster(text) {

    $('#myToast').css({'opacity': 0, top: 0}).find('.toast-text').html(text);

    $("#myToast").fadeIn().animate({
        opacity: 0.9,
        top: "+=30",
    });

    setTimeout(() => {
        $("#myToast").animate({
            opacity: 0,
            top: "-=30",
        }).fadeOut()
    }, 3500)

}

function showNotificationToaster(text) {
    $('#notificationToast').css({'opacity': 0, top: 0}).find('.toast-text').html(text);

    $("#notificationToast").fadeIn().animate({
        opacity: 0.9,
        top: "+=30",
    });

    setTimeout(() => {
        $("#notificationToast").animate({
            opacity: 0,
            top: "-=30",
        }).fadeOut()
    }, 3500)

}


function genericTable(headers, data) {
    var tableHeaders = headers.map(i => `<th scope="col">${i}</th>`).join('');
    let tableBody = [];

    for (let i = 0; i < data.length; i++) {
        let row = data[i];
        tableBody.push('<tr>');

/*        for (const prop in row) {
            if (row.hasOwnProperty(prop)) {
                tableBody.push(`<td>${row[prop]}</td>`);
            }
        }*/

        for (let i = 0; i < headers.length; i++) {
            tableBody.push(`<td>${row[headers[i]]}</td>`);
        }

        tableBody.push('</tr>');
    }

    var table =
        `<table class="table">
            <thead class="thead-dark">
            <tr>${tableHeaders}</tr>
            </thead>
            <tfoot>
            <tr><td colspan="${headers.length}"></td></tr>
        </tfoot>
        <tbody>${tableBody.join('')}</tbody>
        </tr>
        </table>`;

    return table;
}

function setTabByHash(e) {

    var hash = '';

    if (e) {
        hash = e.newURL.split('#')[1];
    } else {
        if (location.href.indexOf('#') > -1) {
            hash = location.href.split('#')[1];
        }
    }

    $('.menu-container').hide();
    if (hash) {
        $(`#${hash}`).show();
    }
}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function getNotifications() {
    return $get(`../../notifications?username=${currentUserSession.username}`, undefined, false)
        .then(data => {
            if (data.Status === 200) {
                for (let i = 0; i < data.Values.Rows.length; i++){
                    showNotificationToaster(data.Values.Rows[i]);
                }
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}