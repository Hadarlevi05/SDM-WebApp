
$(function(){

    var itemSelected = document.getElementsByClassName('menu__group');
    setUIBySelectedItem($(itemSelected));

    addEventListeners();
});

function addEventListeners() {

    getUsers('users', (data) => {
        buildUserList(data.Users);
    });

    getUsers('currentUser', (data) => {

        const user = data.Values.user;

        setCurrentUser(user);
    });

    window.addEventListener("hashchange", function(e){

        setTabByHash(e);

    }, false);

    setTabByHash();
}

function setTabByHash(e) {


    var hash = '';

    if (e) {
        hash =e.newURL.split('#')[1];
    } else {
        if (location.href.indexOf('#') > -1){
            hash = location.href.split('#')[1];
        }
    }


    $('.menu-container').hide();
    $(`#${hash}`).show();
}

function setUIBySelectedItem(itemSelected) {

    if (+itemSelected === 1){
        console.log("123");
    }
}


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

            setTimeout(()=> {
                showLoader(false);
            }, 200);

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

function setCurrentUser(user) {

    $('#userName').html(user.username);
}










