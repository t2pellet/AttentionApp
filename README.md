# AttentionApp

Backend for the app, using firebase cloud functions.

Consists of three major functions

1. Activate
- Sends the notification to the 'receiver' of attention, giving a notification of 'setup complete' and opening the default/main activity (with the attention request button)

2. Request Attention
- Sends the attention request notification to the 'giver' of attention, with the 'receiver' name attached

3. Acknowledge
- Sends the acknowledgement request notification to the 'receiver' of attention, with the 'givre' name attached
