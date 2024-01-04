import firebase_admin
from firebase_admin import credentials, firestore

cred_obj = credentials.Certificate('C:\\Users\\Jim\\Documents\\GitHub\\FourSoulsStats\\service_account_key.json')
firebase_admin.initialize_app(cred_obj)

store = firestore.client()

game_refs = store.collection(u'game_instances')
docs = game_refs.get()
for doc in docs:
    doc_data = doc.to_dict()
    if 'a' in doc_data.keys():
        new_data = {'groupID': doc_data['a'], 'gameID': doc_data['b'], 'gameSize': doc_data['c'],
                    'treasureNum': doc_data['d'], 'playerName': doc_data['e'], 'charName': doc_data['f'],
                    'eternal': doc_data['g'], 'souls': doc_data['h'], 'winner': doc_data['i'], 'coop': doc_data['j'],
                    'solo': doc_data['k'], 'turnsLeft': doc_data['l']}

        game_refs.document(doc.id).set(new_data)

id_refs = store.collection(u'group_ids')
ids = id_refs.get()
for id in ids:
    id_data = id.to_dict()
    if 'a' in id_data.keys():
        new_data = {'id': id_data['a']}
        id_refs.document(id.id).set(new_data)
