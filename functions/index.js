const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.activate = functions.https.onCall((data, ctx) => {
    admin.database().ref(data + '/girlfriend').once('value')
        .then(data => {
            const payload = {
                data: {
                    title: "Setup Complete",
                    body: "Couple configured",
                    type: "setup"
                }
            }
            const options = {
                priority: 'high'
            }
            console.log(data.val());
            admin.messaging().sendToDevice(data.val(), payload, options);
            return 0;
        })
        .catch(error => console.log(error));
});

exports.requestAttention = functions.https.onCall((data, ctx) => {
    admin.database().ref(data).once('value').then(snapshot => {
        const token = snapshot.child('boyfriend').val();
        const name = snapshot.child('girlfriendName').val();
        const payload = {
            data: {
                type: "attention",
                name:  name,
            }
        }
        const options = {
            priority: 'high'
        }
        admin.messaging().sendToDevice(token, payload, options);
        return 0;
    })
    .catch(error => console.log(error));
    return "Attention Requested!";
});

exports.acknowledge = functions.https.onCall((data, ctx) => {
    admin.database().ref(data).once('value').then(snapshot => {
        const token = snapshot.child('girlfriend').val();
        const name = snapshot.child('boyfriendName').val();
        const payload = {
            data: {
                title: "Request Acknowledged",
                body: name + " has acknowledged your request",
                type: "acknowledge"
            }
        }
        const options = {
            priority: 'high'
        }
        admin.messaging().sendToDevice(token, payload, options);
        return 0;
    })
    .catch(error => console.log(error));
});