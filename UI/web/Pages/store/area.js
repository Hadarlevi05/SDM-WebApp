let area = decodeURIComponent(location.search.split('?area=')[1]);
let currentOrder;

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
        debugger;
        buildOrdersHistoryTable(data.Values.Rows);
    });

    getFeedbacks('feedbacks', (data) => {
        buildFeedbacksTable(data.Values.Rows);
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

    $('#btnProceedOrder').on('click', e => {

        proceedOrder((data) => {
            currentOrder = data.Values.Order;
            getSales(data.Values.OrderID, (data) => {

                console.log('sales values', data);
                if (data.Values.Rows.length > 0){
                    showOffers(data.Values.Rows);

                }else{
                    showToaster("No promotions found, please press 'Continue'");
                }
            })
        });

        return false;

    });

    Promise.all([storesPromise, itemsPromise]).then(() => {

        populatePlaceOrderForm();
    });


    /*    $('#exampleModal').find('button').on('click', e => {

            const data = {"data": $('#frmSales').serializeArray()};
            console.log('data', data);

            $('.show-order-details').show()
            $('.proceed-order').hide()
        });*/

    $('#btnShowOrderDetails').on('click', e => {


        getOrderDetails((data) => {

            $('#placeOrderForm').hide();
            $('#orderDetailsTable').show();
            $('#orderDetailsTable').find('.total-amount .sum').html(currentOrder.totalPrice);

            buildOrdersDetailsTable(data.Values.Rows);

        });

        return false;

    });

    $('#btnConfirmOrder').on('click', e => {
        updateOrderToDone(currentOrder, (data) => {

            currentOrder = data.Values.Order;

            showStoresFeedback(currentOrder.storesID);

            showToaster('Thank you for purchasing at super duper market!');
        });
    })

    $(document.body).on('input', 'input[type=range]', (e) => {
        var slider = e.target;

        $('[data-slider-id=' + slider.id + ']').html(slider.value);
    })


}

function updateOrderToDone(order, callback) {
    return $post(`../../orders?area=${area}`, JSON.stringify(order))
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);


            } else {
                console.log('error', data.ErrorMessage);
            }
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

                    let step = 1;
                    debugger;
                    if (item['purchaseType'] === 'WEIGHT') {
                        step = 0.1;
                    }

                    html.push(`
                    <tr>
                    <td>${item['serialnumber']}</td>
                    <td>${item['name']}</td>`
                        + (showPrice ? `<td>${item['price']}</td>` : '') +
                        `<td><input type="number" class="count form-control" step="${step}" name="qs_${item['serialnumber']}" data-storeId="${storeId}" data-price="${item['price']}" value="0"></td>
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

function buildFeedbacksTable(rows) {

    var html = rows.map(row => {
        return `<tr>
                    <td>${row['username']}</td>
                    <td>${row['date']}</td>
                    <td>${row['rate']}</td>
                    <td>${row['message']}</td>                
                </tr>`;
    }).join('')

    $('#feedbacks').find('tbody').data('rows', rows).html(html);
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

    $('#orderHistory').find('tbody').data('rows', rows).html(html);
}


function buildOrdersDetailsTable(rows) {


    var html = rows.map(row => {
        return `<tr>
                    <td>${row['storeID']}</td>
                    <td>${row['storeName']}</td>
                    <td>${row['PPK']}</td>
                    <td>${row['deliveryPrice']}</td>
                    <td>${row['distance']}</td>
                    <td><a href="javascript:void(0);" onclick="showOrderItemsDetails('${row['storeName']} Items', this, '${row['storeID']}');">show ${row['orderItemsDetails'].length} order items</a></td>

                </tr>`;
    }).join('')

    $('#orderDetailsTable').find('tbody').data('rows', rows).html(html);
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


    return $get(`../../sales?area=${area}&orderID=${currentOrder.id}`)
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

function getFeedbacks(action, callback) {

    return $get(`../../feedbacks?area=${area}`)
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

    openModal(title, html, () => {

    });

}

function showOrderItemsDetails(title, td, serialnumber) {
    var rows = $(td).parents('tbody').data('rows');
    var row = rows.filter(r => r.storeID.toString() === serialnumber.toString())[0];

    const html = genericTable(['itemID', 'name', 'purchaseType', 'quantity', 'totalPrice', 'boughtOnSale'], row.orderItemsDetails)


    openModal(title, html, () => {

    });


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

function proceedOrder(callback) {

    const fromValues = $('#placeOrderForm').serializeArray();

    const order = {};
    order.orderStatus = OrderStatusEnum.NEW;
    order.id = 0;
    order.purchaseDate = fromValues.purchaseDate
    order.CustomerLocation = {
        x: fromValues.find(x => x.name === 'locationX').value,
        y: fromValues.find(x => x.name === 'locationY').value
    }
    //fromValues.purchaseDate
    order.purchaseDate = fromValues.find(x => x.name === 'purchaseDate').value;
    order.orderType = fromValues.find(x => x.name === 'typeOfPurchase').value;
    order.storesID = [
        +fromValues.find(x => x.name === 'storeCombo').value
    ]
    order.orderItems = [];
    //fill quantityObject

    for (i = 0; i < fromValues.length; i++) {
        if (fromValues[i].name.indexOf('qs_') === 0) {

            let inputElement = $('[name=' + fromValues[i].name + ']');
            let quantity = +inputElement.val();

            let quantityObject = {};
            if (inputElement.attr('step') == 1) {
                quantityObject.integerQuantity = quantity;
                quantityObject.KGQuantity = 0;
            } else {
                quantityObject.integerQuantity = 0;
                quantityObject.KGQuantity = quantity;
            }
            // `<td><input type="number" class="count form-control" name="qs_${item['serialnumber']}" data-storeId="${storeId}" data-price="${item['price']}" value="0"></td>

            if (quantity > 0) {

                order.orderItems.push({
                    itemId: fromValues[i].name.substring(3),
                    storeId: inputElement.data('storeId'),
                    price: inputElement.data('price'),
                    quantityObject: quantityObject
                });
            }
        }
    }


    // const data = {"data": order};

    return $post(`../../orders?area=${area}`, JSON.stringify(order))
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });


}

function getOrderDetails(callback) {

    const data = {"data": $('#placeOrderForm').serializeArray()};

    return $get(`../../order_details?area=${area}&orderID=${currentOrder.id}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('OrderDetails', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });


}

function showOffers(offers) {

    function getOfferTypeHtml(offerType) {
        if (offerType === 'ONE_OF') {
            return `<h5>Choose one of the following</h5>`;
        } else if (offerType === 'ALL_OR_NOTHING') {
            return `<h5>Choose all or nothing</h5>`;
        } else if (offerType === 'IRRELEVANT') {
            // do nothing
        }
        return '';
    }


    let html = [];

    html.push('<form id="frmSales">');

    for (let i = 0; i < offers.length; i++) {

        let offer = offers[i];


        html.push('<div class="offer-container">');
        html.push(`<h2>${offer.saleName}</h2>`);
        html.push(`${getOfferTypeHtml(offer.operatorType)}`);


        if (offer.operatorType === 'ONE_OF') {
            let operatorId = uuidv4();
            for (let j = 0; j < offer.offers.length; j++) {
                let subOffer = offer.offers[j];
                let id = uuidv4();
                html.push(`<div class="choose-item"><input type="radio" id="${id}" class="discount-radio" name="discountID_${offer.discountID}_${operatorId} value="${subOffer.itemID}"><label for="${id}">get ${subOffer.quantity} of ${subOffer.itemName} only for ${subOffer.forAdditional}</label></div>`);
            }
        } else if (offer.operatorType === 'ALL_OR_NOTHING') {

            html.push('<input type="checkbox" name="discountID_${offer.discountID}" class="discount-checkbox"><label>select all</label>');
            for (let j = 0; j < offer.offers.length; j++) {
                let subOffer = offer.offers[j];
                html.push(`<div class="choose-item">get ${subOffer.quantity} of ${subOffer.itemName} only for ${subOffer.forAdditional}</div>`);
            }

        } else if (offer.operatorType === 'IRRELEVANT') {
            for (let j = 0; j < offer.offers.length; j++) {
                let subOffer = offer.offers[j];
                let id = uuidv4();
                html.push(`<div class="choose-item"><input type="checkbox" id="${id}" class="discount-radio" name="discountID_${offer.discountID}_${subOffer.itemID}"><label for="${id}">get ${subOffer.quantity} of ${subOffer.itemName} only for ${subOffer.forAdditional}</label></div>`);
            }
        }
        html.push('</div>');
    }

    html.push('</form>');

    openModal('On Sale!', html.join(''), () => {

        const data = {"data": $('#frmSales').serializeArray()};
        console.log('data', data);

        // offers

            $('.show-order-details').show();
            $('.proceed-order').hide();

            postSales(currentOrder.id, data, (data) => {
                console.log('post sales success!', data)
            });

            showToaster("To continue order, press 'Continue'");

    });

}

function postSales(orderID, data, callback) {

    return $post(`../../sales?orderID=${orderID}&area=${area}`, JSON.stringify(data))
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function showStoresFeedback(storeIds) {

    let stores = areaData.stores.filter(s => storeIds.includes(s.serialnumber));

    console.log('feedback for stores:', stores);

    let html = [];

    html.push('<form id="frmFeedback">');

    for (let i = 0; i < stores.length; i++) {

        const store = stores[i];

        let id = uuidv4();

        html.push('<div class="feedback-container">');
        html.push(`<h2>${store.name}</h2>`);

        html.push(`<div><lable for="${id}">Choose rating (<span data-slider-id="${id}">1</span> out of 5)</lable><input id="${id}" type="range" name="rate_${store.serialnumber}" min="1" max="5" value="1" class="slider" /></div>`);
        id = uuidv4();
        html.push(`<div><lable for="${id}">enter rating</lable><textarea id="${id}" name="text_${store.serialnumber}"></textarea></div>`);

        html.push('</div>');
    }

    html.push('</form>');


    openModal('Feedback!', html.join(''), () => {

        const textFeedbacks = $('#frmFeedback').serializeArray();
        var feedbacks = {};

        for (let i = 0; i < textFeedbacks.length; i++) {

            const fb = textFeedbacks[i];

            let storeSerialNumber = fb.name.substring(5);

            feedbacks[storeSerialNumber] = feedbacks[storeSerialNumber] || {};

            if (fb.name.indexOf('text_') === 0) {
                feedbacks[storeSerialNumber].message = fb.value;
            } else {
                feedbacks[storeSerialNumber].rate = fb.value;
            }
            feedbacks[storeSerialNumber].date = currentOrder.purchaseDate;
            feedbacks[storeSerialNumber].username = currentUserSession.username;
        }
        postStoresFeedback(Object.values(feedbacks), (data) => {
            console.log('data from post feedbacks', data);

            getOrdersHistory('orders-history', (data) => {
                debugger;
                buildOrdersHistoryTable(data.Values.Rows);
            });

            getFeedbacks('feedbacks', (data) => {
                buildFeedbacksTable(data.Values.Rows);
            });

        });
    });
}

function postStoresFeedback(feedbacks, callback) {
    return $post(`../../feedbacks?area=${area}`, JSON.stringify(feedbacks))
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data);
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function openModal(title, html, action) {
    $('.modal-title').html(title);
    $('#exampleModal').find('.modal-body').html(html);
    $('#btnModal').trigger('click');
    $('#exampleModal').find('button').off('click').on('click', (e) => {
        action(e);
        $('#exampleModal').modal('hide');
        return false;
    });

}
