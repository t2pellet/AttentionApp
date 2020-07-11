const functions = require('firebase-functions');
const admin = require('firebase-admin');
const path = require('path');
const os = require('os');
const fs = require('fs');
admin.initializeApp();
const bucket = admin.storage().bucket();

exports.updateBoyfriend = functions.https.onCall((data, ctx) => {
    bucket.file('boyfriend').save(data);
});
exports.updateGirlfriend = functions.https.onCall((data, ctx) => {
    bucket.file('girlfriend').save(data);
});
exports.requestAttention = functions.https.onCall((data, ctx) => {
    const file = bucket.file('boyfriend');
    file.download((err, contents) => {
        if (err) {
            console.log(err.stack);
        }
        const payload = {
            data: {
                type: "attention",
                title: "Attention Requested",
                body: "Fedora has requested your attention"
            }
        }
        admin.messaging().sendToDevice(contents.toString(), payload);
    });    
    return("Attention requested!");
});
exports.acknowledge = functions.https.onCall((data, ctx) => {
    const file = bucket.file('girlfriend');
    file.download((err, contents) => {
        if (err) {
            console.log(err.stack);
        }
        const payload = {
            data: {
                type: "acknowledge",
                title: "Request Acknowledged",
                body: "Tenzin has acknowledged your request"
            }
        }
        admin.messaging().sendToDevice(contents.toString(), payload);
    })
});