from flask import Flask
from flask import jsonify
from flask import request
from datetime import datetime
from task import notify
import math
import pymysql

app = Flask(__name__)

conn = pymysql.connect(
    host="localhost",
    user="dbuser",
    password="password",
    db="iems5722",
)


@app.route("/api/a3/get_chatrooms")
def get_chatrooms():
    chatroom_result = {}
    conn.ping(reconnect=True)
    mycur = conn.cursor(pymysql.cursors.DictCursor)
    mycur.execute("SELECT * FROM chatrooms")
    conn.commit()
    chatroom_result["status"] = "OK"
    chatroom_result["data"] = mycur.fetchall()
    return jsonify(chatroom_result)


@app.route("/api/a3/get_messages", methods=['GET'])
def get_messages():
    message_result = {}
    chatroom_id = int(request.args.get('chatroom_id'))
    page = int(request.args.get('page'))
    conn.ping(reconnect=True)
    mycur = conn.cursor(pymysql.cursors.DictCursor)
    count_messages = "SELECT COUNT(`message`) FROM `messages` WHERE `chatroom_id` = %s"
    mycur.execute(count_messages, (chatroom_id,))
    message_number = mycur.fetchall()[0]["COUNT(`message`)"]
    if page <= 0 or math.ceil(message_number / 5) < page:
        message_result["message"] = "<error message>"
        message_result["status"] = "ERROR"
    else:
        message_result["status"] = "OK"
        message_result["data"] = {}
        message_result["data"]["current_page"] = page
        message_result["data"]["messages"] = []

        message_query = "SELECT `id`, `chatroom_id`, `user_id`, `name`, `message`, `message_time` FROM `messages` " \
                        "WHERE `chatroom_id` = %s ORDER BY `message_time` DESC LIMIT %s, 5"
        mycur.execute(message_query, (chatroom_id, (page - 1) * 5))
        conn.commit()

        for i in mycur.fetchall():
            message_result["data"]["messages"].append(i)

        message_result["data"]["total_pages"] = math.ceil(message_number / 5)

    return jsonify(message_result)


@app.route("/api/a3/send_message", methods=['POST'])
def post_messages():
    result = {}

    chatroom_id = int(request.form.get("chatroom_id"))
    user_id = int(request.form.get("user_id"))
    name = request.form.get("name")
    message = request.form.get("message")
    chatroom_name = request.form.get("chatroom_name")
    timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    flag = int(str(user_id)[0])

    if chatroom_id < 1 or flag != 1 or name == "" or message == "":
        result["message"] = "<error message>"
        result["status"] = "ERROR"
    else:
        result["status"] = "OK"
        conn.ping(reconnect=True)
        mycur = conn.cursor(pymysql.cursors.DictCursor)
        insert_message = "INSERT INTO `messages` (`chatroom_id`, `user_id`, `name`, `message`, `message_time`)" \
                         "VALUES (%s, %s, %s, %s, %s)"
        mycur.execute(insert_message, (chatroom_id, user_id, name, message, timestamp))
        conn.commit()

        select_token_query = "SELECT token FROM push_tokens"
        mycur.execute(select_token_query)
        while 1:
            token_json = mycur.fetchone()
            if token_json is None:
                break
            else:
                token = token_json['token']
                notify.delay(chatroom_id, chatroom_name, name, message, token)

        return jsonify(result)


@app.route("/api/a4/submit_push_token", methods=['POST'])
def submit_push_token():
    user_id = int(request.form.get("user_id"))
    token = request.form.get("token")

    if token == None or user_id == None:
        return jsonify(status="ERROR", message="parameters missing!")
    else:
        conn.ping(reconnect=True)
        mycur = conn.cursor(pymysql.cursors.DictCursor)
        insert_query = "INSERT INTO `push_tokens` (`user_id`,`token`) VALUES (%s,%s)"
        params = (user_id, token)
        mycur.execute(insert_query, params)
        conn.commit()

        return jsonify(status="OK")


if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=8000)
