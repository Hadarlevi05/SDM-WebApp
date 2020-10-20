let area = decodeURIComponent(location.search.split('?area=')[1]);

let areaData = {
    items: null,
    stores: null
}


$(function () {
    /*    var itemSelected = document.getElementsByClassName('menu__group');
        setUIBySelectedItem($(itemSelected));*/

    addEventListeners();

    setPermission();

    setCurrentUser(currentUserSession);
});

function addEventListeners() {

    const storesPromise = getStores('stores', (data) => {

        areaData.stores = data.Values.Rows;

        buildStoresTable(data.Values.Rows);
    });

    const itemsPromise = getItems('items', (data) => {

        areaData.items = data.Values.Rows;

        buildItemsTable(data.Values.Rows);
    });

    getOrdersHistory('orders-history', (data) => {
        buildOrdersHistoryTable(data.Values.Rows);
    });

    window.addEventListener("hashchange", function (e) {
        setTabByHash(e);
    }, false);
    setTabByHash();

    $('[name=typeOfPurchase]').on('change', (e) => {
        let value = e.target.value;

        // $('.purchase-type-dynamic').hide();
        $('.purchase-type-static').hide();

        if (value) {
            $(`.${value}`).show();

            if (value === 'purchase-type-dynamic') {
                populateItemsTable();
            } else {
                $('[name=storeCombo]').trigger('change');
            }
        }


    })

    $('[name=storeCombo]').on('change', e=> {

        // load items of specific store
        if (e.target.value && e.target.value !== 'Select store') {
            populateItemsTable(e.target.value);
        }

    });

    Promise.all([storesPromise, itemsPromise]).then(() => {

        populatePlaceOrderForm();
    });
}

function populatePlaceOrderForm() {
    let html = [];
    for (let i = 0; i < areaData.stores.length; i++) {
        let store = areaData.stores[i];

        html.push(`<option value="${store.serialnumber}">${store.name}</option>`);
    }
    $('[name=storeCombo]').html(html.join('')).trigger('change');

}

function populateItemsTable(storeId) {

    if (!storeId) {
        storeId = -1;
        console.log('load storeId ' + storeId);
    } else {
        console.log('load all stores');
    }

    return $get(`../../items?area=${area}&store=${storeId}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);

                html = [];
                for (let i = 0; i < data.Values.Rows.length; i++) {
                    let item = data.Values.Rows[i];

                    html.push(`
                    <tr>
                    <td>${item['serialnumber']}</td>
                    <td>${item['name']}</td>
                    <td><input type="number" class="count" name="qs_${item['serialnumber']}" value="0"></td>
                    </tr>
                    `);
                }
                $('#placeOrderForm tbody').html(html.join(''));

                $('.purchase-type-dynamic').show();
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
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

function buildOrdersHistoryTable(rows) {

    var html = rows.map(row => {
        return `<tr>
                    <td>${row['serialnumber']}</td>
                    <td>${row['date']}</td>
                    <td>${row['location']}</td>
                    <td>${row['numOfStores']}</td>
                    <td>${row['numOfItems']}</td>
                    <td>${row['totalItemsPrice']}</td>
                    <td>${row['deliveryPrice']}</td>
                    <td>${row['totalOrderPrice']}</td>
                </tr>`;
    }).join('')

    $('#orders-history').find('tbody').data('rows', rows).html(html);
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
    return $get(`../../items?area=${area}&store=-1`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function getOrdersHistory(action, callback) {


    return $get(`../../order-history?area=${area}`)
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

    const html = genericTable(['serialnumber', 'numOfSoldItems', 'price', 'name', 'purchaseType'], row.items)

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
                showToaster("Store added with great success!");
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function placeOrder() {

    const data = $('#placeOrderForm').serializeArray();

    console.log('send data to server', data);

}
