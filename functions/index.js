const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.updateBoyfriend = functions.https.onCall((data, ctx) => {
    const payload = {
        topic: "girlfriend",
        data: {
            title: "Setup Complete",
            body: "Couple configured",
            type: "boyfriend"
        }
    }
    admin.messaging().send(payload);
});
exports.requestAttention = functions.https.onCall((data, ctx) => {
    const payload = {
        topic: "boyfriend",
        data: {
            title: "Attention Requested",
            body: "Fedora has requested your attention",
            type: "attention"
        }
    }
    admin.messaging().send(payload);  
    return("Attention requested!");
});
exports.acknowledge = functions.https.onCall((data, ctx) => {
    const payload = {
        topic: "girlfriend",
        data: {
            title: "Request Acknowledged",
            body: "Tenzin has acknowledged your request",
            type: "acknowledge"
        }
    }
    admin.messaging().send(payload);
});