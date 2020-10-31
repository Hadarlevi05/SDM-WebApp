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

        buildItemsDropDown(data.Values.Rows);
    });

    getOrdersHistory('orders-history', (data) => {

        $('#orderHistoryForCustomer').hide();
        $('#orderHistoryForStoreOwner').hide();

        if (currentUserSession.userType === 'CUSTOMER') {
            $('#orderHistoryForCustomer').show();
            buildOrdersHistoryTableForCustomer(data.Values.Rows);
        } else if (currentUserSession.userType === 'STORE_OWNER') {
            $('#orderHistoryForStoreOwner').show();
            buildOrdersHistoryTableForStoreOwner(data.Values.Rows);
        }
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


        let valid = $('#placeOrderForm')[0].checkValidity();

        if (!valid) {

            showToaster('Please fill all fields!');
            return false;
        }


        proceedOrder((data) => {
            currentOrder = data.Values.Order;
            getSales(data.Values.OrderID, (data) => {

                console.log('sales values', data);
                if (data.Values.Rows.length > 0) {
                    showOffers(data.Values.Rows);

                } else {
                    showToaster("No promotions found, please press 'Continue'");
                    $('.show-order-details').show();
                    $('.proceed-order').hide();
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
            $('#orderDetailsTable').find('.total-amount .sum').html(currentOrder.totalPrice.toFixed(2));

            buildOrdersDetailsTable(data.Values.Rows);

        });

        return false;

    });


    $('#btnAddStore').on('click', e => {

        buildAddStoreModal();
        /*        getOrderDetails((data) => {

                    $('#placeOrderForm').hide();
                    $('#orderDetailsTable').show();
                    $('#orderDetailsTable').find('.total-amount .sum').html(currentOrder.totalPrice);

                    buildOrdersDetailsTable(data.Values.Rows);

                });

                return false;*/

    });

    $('#btnConfirmOrder').on('click', e => {
        updateOrderToDone(currentOrder, (data) => {

            currentOrder = data.Values.Order;

            showStoresFeedback(currentOrder.storesID);

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

    let typeOfPurchase = '';

    if (!storeId || storeId === -1) {
        storeId = -1;
        typeOfPurchase = 'dynamic';
        console.log('load storeId ' + storeId);
    } else {
        typeOfPurchase = 'static';
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

                    if (item['purchaseType'] === 'WEIGHT') {
                        step = 0.1;
                    }

                    html.push(`
                    <tr>
                    <td>${item['serialnumber']}</td>
                    <td>${item['name']}</td>`
                        + (showPrice ? `<td>${item['price']}</td>` : '') +
                        `<td><input type="number" class="count form-control" step="${step}" name="qs_${typeOfPurchase}_${item['serialnumber']}" data-storeId="${storeId}" data-price="${item['price']}" value="0"></td>
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
                    <!-- <td>${row['TotalCostOfDeliveriesFromStore']}</td>-->
                    <td>${row['numOfTakenOrders']}</td>
                    <td>${row['itemsCost']}</td>
                    <td>${row['PPK']}</td>
                </tr>`;
    }).join('')

    $('#storesTable').find('tbody').data('rows', rows).html(html);
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


function buildOrdersHistoryTableForCustomer(rows) {
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
                     <td><a href="javascript:void(0);" onclick="showOrderHistoryItemsDetails('${row['orderItems'].length} Order Items', this, '${row['serialnumber']}');">show ${row['orderItems'].length} order items</a></td>
              
                </tr>`;
    }).join('')

    $('#orderHistoryForCustomer').find('tbody').data('rows', rows).html(html);
}


function buildOrdersHistoryTableForStoreOwner(rows) {

    /*
        customerLoc: "[1,1]"
        customerName: "hadar"
        date: "19/00-12:00 "
        deleveryPrice: "30.00"
        id: 1
        items: (2) [{…}, {…}]
        itemsCost: "120.00"
        numOfItems: "7.00"
        totalPrice: "150.00"
        */


    var html = rows.map(row => {

        for (let i = 0; i < row.storeOrders.length; i++) {
            row.storeOrders[i].openItems =

                `<a href="javascript:void(0);" onclick="showSoterOrderItems(this, '${row['storeName']}', '${row.storeOrders[i].id}')">
                ${row.storeOrders[i].items.length} items
            </a>`;
        }

        return `<div class="center-70"><h2>${row['storeName']}</h2></div>
                ${genericTable(['id', 'date', 'customerName', 'customerLoc', 'numOfItems', 'itemsCost', 'deleveryPrice', 'totalPrice', 'openItems'], row.storeOrders)}
                `;
    }).join('')

    $('#orderHistoryForStoreOwner').find('.display-stores').data('rows', rows).html(html);
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

    const html = genericTable(['itemID', 'name', 'purchaseType', 'quantity', 'price', 'totalPrice', 'boughtOnSale'], row.orderItemsDetails)

    openModal(title, html, () => {

    });
}

function showOrderHistoryItemsDetails(title, td, serialnumber) {
    var rows = $(td).parents('tbody').data('rows');
    var row = rows.filter(r => r.serialnumber.toString() === serialnumber.toString())[0];

    const html = genericTable(['itemID', 'name', 'purchaseType', 'storeID', 'storeName', 'quantity', 'totalPricePerItem', 'totalPrice', 'boughtOnSale'], row.orderItems)


    openModal(title, html, () => {

    });


}

function insertNewStore(callback) {

    const postData = {
        name: $('#exampleModal').find('input[name=insertTXT]').val(),
        area: area,
        username: currentUserSession.username,
        locationX: $('#exampleModal').find('input[name=insertLocationX]').val(),
        locationY: $('#exampleModal').find('input[name=insertLocationY]').val(),
        items: $('#exampleModal').find('select[name=selectItemsInArea]').val(),
        ppk: $('#exampleModal').find('input[name=insertPPK]').val()
    };

    return $post(`../../stores`, postData)
        .then(data => {
            if (data.Status === 200) {
                showToaster("Store added with great success!");
                callback(data)
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

    let prefix = order.orderType === 'purchase-type-dynamic' ? 'dynamic' : 'static';

    //fill quantityObject

    for (i = 0; i < fromValues.length; i++) {
        if (fromValues[i].name.indexOf(`qs_${prefix}`) === 0) {


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

            let price = 0;
            if (inputElement.data('price') !== `undefined`) {
                price = inputElement.data('price');
            }

            if (quantity > 0) {

                order.orderItems.push({
                    itemId: fromValues[i].name.split('_')[2],
                    storeId: inputElement.data('storeId'),
                    price: price,
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
                html.push(`<div class="choose-item"><input type="radio" id="${id}" class="discount-radio" name="discountID_${offer.discountID}_${operatorId}" value="${subOffer.itemID}"><label for="${id}">get ${subOffer.quantity} of ${subOffer.itemName} only for ${subOffer.forAdditional}</label></div>`);
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
            currentOrder = data.Values.Order;
            console.log('post sales success!', data);

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

                if (currentUserSession.userType === 'STORE_OWNER') {
                    buildOrdersHistoryTableForStoreOwner(data.Values.Rows);
                } else if (currentUserSession.userType === 'CUSTOMER') {
                    //$('#orderHistoryForCustomer').show()
                    buildOrdersHistoryTableForCustomer(data.Values.Rows);
                }


            });

            getFeedbacks('feedbacks', (data) => {
                buildFeedbacksTable(data.Values.Rows);
            });
            showToaster('Thank you for purchasing at super duper market!');

            location.reload();

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

function openModal(title, html, action, options) {

    options = options || {};

    $('.modal-title').html(title);
    $('#exampleModal').find('.modal-body').html(html);

    if (options.hideCloseButton) {
        $('.modal-footer button').hide();
    } else {
        $('.modal-footer button').show();
    }

    $('#btnModal').trigger('click');
    $('#exampleModal').find('button').off('click').on('click', (e) => {
        action(e);
        $('#exampleModal').modal('hide');
        return false;
    });

    if (options.onModalOpen) {
        options.onModalOpen();
    }


}

function buildAddStoreModal() {


    let html = $("#insertStore").html();

    openModal("Add new store", html, () => {

    }, {
        hideCloseButton: true,
        onModalOpen: () => {

            $('.modal form').on('submit', e => {

                $('#exampleModal').find('input[name=insertTXT]').parents('p').children('.error').hide();

                let formStoreName = $('#exampleModal').find('input[name=insertTXT]').val();

                if (areaData.stores.filter(i => i.name === formStoreName).length > 0) {

                    $('#exampleModal').find('input[name=insertTXT]').parents('p').children('.error').show();

                } else {
                    insertNewStore((data) => {
                        getStores('stores', (data2) => {

                            areaData.stores = data2.Values.Rows;

                            buildStoresTable(data2.Values.Rows);
                        });
                    });
                    $('#exampleModal').modal('hide');


                }



                return false;
            });



        }
    });
}

function buildItemsDropDown(rows) {
    var html = rows.map(row => {
        return `
                <option value=${row['serialnumber']}>${row['serialnumber']}. ${row['name']}</option>
                `;
    }).join('')

    $('#stores').find('select').append(html);
}

function getUsers(action, callback) {

    return $get(`../../users?action=${action}`, null, false)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function showSoterOrderItems(element, storeName, orderId) {

    let data = $(element).parents('.display-stores:eq(0)').data('rows');

    // console.log('store data ', data, orderId);

    let storeData = data.filter(i => i.storeName === storeName)[0];

    let order = storeData.storeOrders.filter(i => i.id === +orderId)[0];

    //console.log('order', order);

    const html = genericTable(['itemID', 'name', 'purchaseType', 'quantity', 'totalPricePerItem', 'totalPrice', 'boughtOnSale'], order.items)

    openModal('items', html, () => {

    });

}