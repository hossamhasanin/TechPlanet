const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.firestore.document("posts/{post_id}").onWrite((data , context) => {
    const post_id = context.params.post_id;
    const title = data.after.data().title;
    const user_id = data.after.data().userId;
    const description = data.after.data().description;
    const topic = "newPost";

    const payload = {
            notification: {
                title: title,
                body: description,
                icon: "default"
            },
            data: {
                userId: user_id
            } 
        }

    return admin.messaging().sendToTopic(topic , payload).then(result => {
        console.log("Notification sent successfully");
    }).catch(err => {
        console.log("Error : " + err);
    });    

    //console.log("Hello !");

});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
