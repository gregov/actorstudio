from flask import Flask, request
import subprocess
import uuid
import json

data = {}
processed = []

# Word count
# Sentence count
# Letter count
# md5sum
# Reverse


def generate_news():
    doc_id = uuid.uuid1().int
    document = subprocess.check_output('fortune')
    data[doc_id] = document
    return doc_id


app = Flask(__name__)


@app.route("/", methods=["GET"])
def index():
    res = ""
    for k in data.values():
        res += k + "<br />"

    res += "{} documents generated <br />".format(len(data.keys()))
    res += "{} documents processed <br />".format(len(processed))
    return res


@app.route("/new", methods=["GET"])
def new():
    return json.dumps(generate_news())


@app.route("/news/<int:doc_id>", methods=["GET"])
def get_document(doc_id):
    document = data.get(doc_id)
    if document:
        return document
    else:
        return "Document {} not found".format(doc_id), 404


@app.route("/submit", methods=["POST"])
def submit():
    doc_id = request.form.get('doc_id', type=int)
    if data.get(doc_id):
        if doc_id not in processed:
            processed.append(doc_id)
            return "OK"
        else:
            return "Already processed", 400
    else:
        return "Document {} not found".format(doc_id), 404


if __name__ == "__main__":
    app.run()
