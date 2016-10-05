# actorstudio
Playing with akka actors.

0. install the requirements
      brew install fortune
      pip install flask
1. Open the firehose: python firehose.py
2. monitor it: http://localhost:5000
3. Start pique app


Overview:

The entire line is driven by the last element, Kanban style.

Indexer asks Fetcher for a document to index
Fetcher asks Broker for a document id to fetch
Broker pull a document id from the firehose

The bookkeeper keep track of every document id that is processing
if a document id is still processing after 25 sec, the bookkeeper resend
the document id to the Fetcher


Principles:
No stock, hence the Kanban style line.
Every actor will eventually fail, the system should self heal.
No document should ever been lost during the process.
