$(function () {

    /*    var itemSelected = document.getElementsByClassName('menu__group');
        setUIBySelectedItem($(itemSelected));*/

    addEventListeners();

    setPermission();
});

function setPermission() {

    if (!currentUserSession) {
        redirectToLogin();
    }

    if (currentUserSession.userType === 'CUSTOMER') {
        $('[role=store-owner-permission]').hide();
    }

}


function addEventListeners() {

    getUsers('users', (data) => {
        buildUserList(data.Users);
    });

    getUsers('currentUser', (data) => {

        const user = data.Values.user;

        setCurrentUser(user);
    });

    window.addEventListener("hashchange", function (e) {

        setTabByHash(e);
    }, false);

    $('#FileInput').on('change', (e) => {

        loadFile(e, (data) => {

            alert(data.ErrorMessage);

            getSDMs('allUserConfig', (data) => {
                buildStoreAreasTable(data.Values.Rows);
            });
        });
    });


    getSDMs('allUserConfig', (data) => {
        buildStoreAreasTable(data.Values.Rows);
    });

    setTabByHash();
}

function getSDMs(action, callback) {

    showLoader(true);

    return $get(`../../superdupermarket?action=${action}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)


            } else {
                console.log('error', data.ErrorMessage);
            }
            showLoader(false);
        });
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

/*function setUIBySelectedItem(itemSelected) {

    if (+itemSelected === 1) {
        console.log("123");
    }
}*/


function getUsers(action, callback) {

    showLoader(true);

    return $get(`../../users?action=${action}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)


            } else {
                console.log('error', data.ErrorMessage);
            }


            showLoader(false);


        });
}


/*
    Utility methods
 */
function getUserTypeText(userType) {

    if (userType === 'CUSTOMER') {
        return 'Customer';
    } else if (userType === 'STORE_OWNER') {
        return 'Store Owner';
    }
}


/*
    UI
    ==
 */
function buildUserList(users) {

    var html = users.map(user => {
        return `<tr><td>${user.username}</td><td>${getUserTypeText(user.userType)}</td></tr>`;
    }).join('')

    $('#registeredUsers').find('tbody').html(html);
}

function buildStoreAreasTable(rows) {

    var html = rows.map(row => {
        return `<tr>
                    <td>${row['storeowner']}</td>
                    <td>${row['area']}</td>
                    <td>${row['itemstypes']}</td>
                    <td>${row['storesnumber']}</td>
                    <td>${row['ordersnumber']}</td>
                    <td>${row['avgordersprice']}</td>
                </tr>`;
    }).join('')

    $('#storeAreas').find('tbody').html(html);
}

function setCurrentUser(user) {

    $('#userName').html(user.username);
}










