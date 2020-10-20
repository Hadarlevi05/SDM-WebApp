let area = decodeURIComponent(location.search.split('?area=')[1]);

$(function () {
    /*    var itemSelected = document.getElementsByClassName('menu__group');
        setUIBySelectedItem($(itemSelected));*/

    addEventListeners();

    setPermission();

    setCurrentUser(currentUserSession);
});

function addEventListeners() {

    getStores('stores', (data) => {
        buildStoresTable(data.Values.Rows);
    });

    getItems('stores', (data) => {
        buildItemsTable(data.Values.Rows);
    });

    window.addEventListener("hashchange", function (e) {
        setTabByHash(e);
    }, false);
    setTabByHash();

    $('[name=typeOfPurchase]').on('change', (e) => {
        let value = e.target.value;

        $('.purchase-type-dynamic').hide();
        $('.purchase-type-static').hide();

        $(`.${value}`).show();
    })
}

function buildStoresTable(rows) {
    var html = rows.map(row => {
        return `<tr>
                    <td>${row['serialnumber']}</td>
                    <td>${row['name']}</td>
                    <td>${row['owner']}</td>
                    <td>${row['location']}</td>
                    <td><a href="javascript:void(0);" onclick="showStoresItems('${row['name']} Items', this, '${row['serialnumber']}');">show ${row['items'].length} items</a></td>
                    <td>${row['PPK']}</td>
                    <td>${row['TotalCostOfDeliveriesFromStore']}</td>
                    <td>-</td>
                </tr>`;
    }).join('')

    $('#stores').find('tbody').data('rows', rows).html(html);
}

function buildItemsTable(rows) {

    var html = rows.map(row => {
        return `<tr>
                    <td>${row['serialnumber']}</td>
                    <td>${row['name']}</td>
                    <td>${row['purchaseType']}</td>
                    <td>${row['numOfStoresSellingItems']}</td>
                    <td>${row['averagePrice']}</td>
                    <td>${row['soldItemsAmount']}</td>
                </tr>`;
    }).join('')

    $('#items').find('tbody').data('rows', rows).html(html);
}

function getStores(action, callback) {
    return $get(`../../stores?area=${area}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function getItems(action, callback) {
    return $get(`../../items?area=${area}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function showStoresItems(title, td, serialnumber) {
    var rows = $(td).parents('tbody').data('rows');
    var row = rows.filter(r => r.serialnumber.toString() === serialnumber.toString())[0];

    const html = genericTable(['serialnumber','numOfSoldItems','price','name','purchaseType'], row.items)

    $('.modal-title').html(title);


    $('#btnModal').trigger('click');
    $('#exampleModal').find('.modal-body').html(html);

}

function insertStore() {
    const postData = {
        name: $('#stores').find('input[name=insertTXT]').val(),
        area: area,
        username: currentUserSession.username,
        locationX: $('#stores').find('input[name=insertLocationX]').val(),
        locationY: $('#stores').find('input[name=insertLocationY]').val(),
        ppk: $('#stores').find('input[name=insertPPK]').val(),
    };

    return $post(`../../stores`, postData)
        .then(data => {
            if (data.Status === 200) {
                showToaster ("Store added with great success!");
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}