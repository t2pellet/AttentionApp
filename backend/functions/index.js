const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onSetup = functions.database.instance("attentionapp-9137c")
    .ref("/{coupleId}/RECEIVER/token").onWrite((data, ctx) => {
      functions.logger.log("Wrote new RECEIVER");
      const key = data.after.ref.parent.parent.key;
      admin.database().ref(key).once("value").then((snapshot) => {
        const payload = {
          data: {
            type: "setup",
            name: snapshot.child("RECEIVER").child("name").val(),
          },
          token: snapshot.child("SENDER").child("token").val(),
        };
        admin.messaging().send(payload).then(() => {
          functions.logger.log("Sent payload to SENDER");
        });
      }).catch((error) => functions.logger.error(error));
    });

exports.request = functions.https.onCall((data, ctx) => {
  admin.database().ref(data).once("value").then((snapshot) => {
    const payload = {
      data: {
        type: "request",
      },
      token: snapshot.child("RECEIVER").child("token").val(),
    };
    admin.messaging().send(payload);
  }).catch((error) => console.log(error));
  return "Attention Requested!";
});

exports.respond = functions.https.onCall((data, ctx) => {
  admin.database().ref(data).once("value").then((snapshot) => {
    const payload = {
      data: {
        type: "response",
      },
      token: snapshot.child("SENDER").child("token").val(),
    };
    admin.messaging().send(payload);
  }).catch((error) => console.log(error));
});
