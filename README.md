This adds a feature to TestChronoChat that allows you to simulate
'participants' users each sending 'numMessages' messages in one chat
room where each user's chat messages are separated at a random number of
ms apart ranging anywhere from 10ms to 10,000ms.

test TestChronotChat with cmd: 'mvn -q -e test -DclassName=TestChronoChat -Dparticipants=5 -DnumMessages=8'

5 and 8 are sample arguments.
- participants is the number of users that will be firing messages off in
the chat room,
- numMessages is the number of messages each user will fire off.

The delay between each user's chat as they fire of numMessages is
anywhere between 10ms and 10,000ms.

Each user's thread waits for all other user threads to fire off all
messages in chat room then waits a bit to process any last incoming 
messages before terminating.

During runtime what each user sent as well as some some of what they see print.
At the end of each user's thread each user prints off an array of summary
statistics for their conversation with each other user. It is an array
of offsets, as well as the totals. Offsets are -1 = messages lost, 0 =
correct number of messages received (1), and 1+ = some number of
duplicates received.

After all threads have been shut down. There is an accumulation
statistic that prints. This shows the total number of messages
fired, the expected total number of messages fired, the total number of
duplicates, and the total number of lost messages.

The total number of messages that the test will send in the chat room is
calculated by (participants - 1) * numMessages * participants.
