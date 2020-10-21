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

        $('.purchase-type-dynamic').hide();
        $('.purchase-type-static').hide();

        if (value) {
            $(`.${value}`).show();

            if (value === 'purchase-type-dynamic') {
                populateItemsTable(-1, '.purchase-type-dynamic tbody');
                $('.purchase-type-dynamic').show();
            } else {
                $('[name=storeCombo]').trigger('change');
                $('.purchase-type-static').show();
            }
        }


    })

    $('[name=storeCombo]').on('change', e => {

        // load items of specific store
        if (e.target.value) {
            populateItemsTable(e.target.value, '.purchase-type-static tbody', true);
        }
    });

    $('#btnPlaceOrder').on('click', e => {

        placeOrder((data) => {
            getSales(data.Values.OrderID, (data) => {

                console.log('sales values', data);

                showOffers(data.Values.Rows);
            })
        });

        return false;

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

function populateItemsTable(storeId, tableTbody, showPrice) {

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
                    <td>${item['name']}</td>`
                        + (showPrice ? `<td>${item['price']}</td>` : '') +
                        `<td><input type="number" class="count form-control" name="qs_${item['serialnumber']}" value="0"></td>
                    </tr>
                    `);
                }
                $(tableTbody).html(html.join(''));

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

function getSales(orderID, callback) {


    return $get(`../../sales?area=${area}&orderID=${orderID}`)
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

function placeOrder(callback) {

    const data = {"data": $('#placeOrderForm').serializeArray()};

    return $post(`../../orders?area=${area}`, JSON.stringify(data))
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });


}

function showOffers(offers) {


    let html = [];

    for (let i = 0; i < offers.length; i++) {

        let offer = offers[i];

        html.push('<div>');

        html.push(`<h2>${offer.saleName}</h2>`);
        html.push(`<div>${offer.operatorType}</div>`);


        if (offer.operatorType === 'ONE_OF') {
            for (let j = 0; j < offer.offers.length; j++) {
                let subOffer = offer.offers[j];
                html.push(`<div><input type="radio"  class="discount-radio" name="discountID_${offer.discountID}"><label>get ${subOffer.quantity} of ${subOffer.itemName} only for ${subOffer.forAdditional}</label></div>`);
            }
        } else if (offer.operatorType === 'ALL_OR_NOTHING') {


            html.push('<input type="checkbox" name="discountID_${offer.discountID}" class="discount-checkbox"><label>select all</label>')
            for (let j = 0; j < offer.offers.length; j++) {
                let subOffer = offer.offers[j];
                html.push(`<div>get ${subOffer.quantity} of ${subOffer.itemName} only for ${subOffer.forAdditional}</div>`);
            }

        }
    }
    html.push('</div>');


    $('.modal-title').html('On Sale!');
    $('#btnModal').trigger('click');
    $('#exampleModal').find('.modal-body').html(html.join(''));
    // $('#dvOffers').html(html.join(''));


}