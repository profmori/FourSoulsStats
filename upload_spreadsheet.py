import pandas
import firebase_admin
from firebase_admin import credentials, firestore

cred_obj = credentials.Certificate('C:\\Users\\Jim\\Documents\\GitHub\\FourSoulsStats\\service_account_key.json')
firebase_admin.initialize_app(cred_obj)

store = firestore.client()

game_refs = store.collection(u'game_instances')

values = pandas.read_csv("Four Souls Stats - Player Record.csv")
player = values.transpose().to_dict()
start_date = 1616803200 #27-Mar-2021
end_date = 1623974400 #18-Jun-2021
i = 1
total_games = sum([player[curr_player]['Winner?'] for curr_player in player])
curr_player = player[i-1]
game_num = 0
group_start = 0
while type(curr_player['Player']) == str:
    game_id = f'40REGE{start_date + (game_num * (end_date - start_date) // (total_games - 1))}'
    new_data = {'groupID': '40REGE', 'gameID': game_id, 'gameSize': int(curr_player['Game Size']),
                'treasureNum': 0, 'playerName': curr_player['Player'].lower(), 'charName': curr_player['Character'].lower(),
                'eternal': None, 'souls': int(curr_player['Souls Gained']), 'winner': curr_player['Winner?'], 'coop': False,
                'solo': False, 'turnsLeft': -1}
    doc_id = f'{game_id[7::]}{str([ord(x) for x in curr_player["Player"].lower()]).replace(", ","").strip("[]")}'
    print(doc_id)
    game_refs.document(doc_id).set(new_data)
    
    if group_start + curr_player['Game Size'] == i:
        group_start = i
        game_num += 1

    curr_player = player[i]
    i += 1
