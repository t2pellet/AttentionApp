const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.setToken = functions.database.ref('boyfriend').onWrite((snapshot, context) => {
    const payload = {
        topic: "setup",
        data: {
            title: "Setup Complete",
            body: "Couple configured",
            type: "setup"
        }
    }
    admin.messaging().send(payload);
    return 0;
});
exports.requestAttention = functions.https.onCall((data, ctx) => {
    admin.database().ref('boyfriend').once('value')
        .then(data => {
            const payload = {
                data: {
                    title: "Attention Requested",
                    body: "Fedora has requested your attention",
                    type: "attention"
                }
            }
            const options = {
                priority: 'high'
            }
            admin.messaging().sendToDevice(data.val(), payload, options);
            return 0;
        })
        .catch(error => console.log(error));
    return "Attention Requested!";
});
exports.acknowledge = functions.https.onCall((data, ctx) => {
    admin.database().ref('girlfriend').once('value')
    .then(data => {
        const payload = {
            data: {
                title: "Request Acknowledged",
                body: "Tenzin has acknowledged your request",
                type: "acknowledge"
            }
        }
        const options = {
            priority: 'high'
        }
        admin.messaging().sendToDevice(data.val(), payload, options);
        return 0;
    })
    .catch(error => console.log(error));
});