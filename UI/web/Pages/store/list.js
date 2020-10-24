$(function () {

    /*    var itemSelected = document.getElementsByClassName('menu__group');
        setUIBySelectedItem($(itemSelected));*/
    addEventListeners();

    setPermission();

    setCurrentUser(currentUserSession);
});


function addEventListeners() {

    getUsers('users', (data) => {
        buildUserList(data.Users);
    });

/*    getUsers('currentUser', (data) => {
        const user = data.Values.user;
        setCurrentUser(user);
    });*/

    const user = userSession();

    getTransactions(user.username, (data) => {
        buildTransactionsTable(data.Transactions);
    });

    window.addEventListener("hashchange", function (e) {
        setTabByHash(e);
    }, false);
    setTabByHash();

    $('#FileInput').on('change', (e) => {

        loadFile(e, (data) => {

            showToaster(data.ErrorMessage);

            getSDMs('allUserConfig', (data) => {
                buildStoreAreasTable(data.Values.Rows);
            });
        });
    });

    getSDMs('allUserConfig', (data) => {
        buildStoreAreasTable(data.Values.Rows);
    });
}

function getSDMs(action, callback) {

    return $get(`../../superdupermarket?action=${action}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)


            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}


/*function setUIBySelectedItem(itemSelected) {

    if (+itemSelected === 1) {
        console.log("123");
    }
}*/

function getUsers(action, callback) {

    return $get(`../../users?action=${action}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)
            } else {
                console.log('error', data.ErrorMessage);
            }
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
                    <td><a href="area.html?area=${row['area']}">${row['area']}</a></td>
                    <td>${row['itemstypes']}</td>
                    <td>${row['storesnumber']}</td>
                    <td>${row['ordersnumber']}</td>
                    <td>${row['avgordersprice']}</td>
                </tr>`;
    }).join('')

    $('#storeAreas').find('tbody').html(html);
}

function buildTransactionsTable(rows) {

    var html = rows.map(row => {
        return `<tr>
                    <td>${row['transactionType']}</td>
                    <td>${row['transactionDate']}</td>
                    <td>${row['sumOfTransaction']}</td>
                    <td>${row['balanceBeforeAction']}</td>
                    <td>${row['balanceAfterAction']}</td>                   
                </tr>`;
    }).join('')

    $('#account').find('tbody').html(html);
}


function getTransactions(username, callback) {

    return $get(`../../transactions?username=${username}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function chargeMoney() {

    const postData = {
        sumOfTransaction: $('#account').find('input[name=txtSum]').val(),
        transactionType: 'CHARGE_MONEY',
    };

    return $post(`../../transactions`, postData)
        .then(data => {
            if (data.Status === 200) {
                getTransactions(currentUserSession.username, (data) => {
                    buildTransactionsTable(data.Transactions);
                });
                $('#account').find('input[name=txtSum]').val('');
                showToaster ("Money charged successfully");
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}




