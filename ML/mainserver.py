from flask import Flask, request, jsonify
from io import StringIO
import pandas as pd
import numpy as np
import sklearn
from sklearn import preprocessing
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import RandomForestRegressor
import matplotlib.pyplot as plt

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def index():
	lines = request.get_json().get("file")
	lines = lines.split('\r\n')[:-1]
	file_main = ""
	for line in lines:
	    file_main = file_main+line+'\n'
	file_main2 = StringIO(file_main)
	df = pd.read_csv(file_main2,sep=",")
	x = df[df['SUBDIVISION'] == request.get_json().get("state")]
	X = x[request.get_json().get("month")]
	X = X.dropna()
	'''print(len(X))
	print(list(X))
	X=X[0:100]
	print("////////////10years subtracted//////////")'''
	print(len(X))
	print(list(X))
	number_of_data = len(X)
	list_one = []
	list_two = []
    #print(list(X))
	for i in range(len(X)-10):
		list_one.append(X[i:i+10])
		list_two.append(X[i+10:i+11])

	X = np.stack(list_one)
	y = np.stack(list_two).reshape(-1,1)

	labelencoder_X = LabelEncoder()
	X[:,0]= labelencoder_X.fit_transform(X[:,0])

	scaler = preprocessing.MinMaxScaler()
	X = scaler.fit_transform(X)
	y = scaler.fit_transform(y)

	X = np.array(X)
	y = np.array(y)

	regressor = RandomForestRegressor(n_estimators=10, max_depth=None, max_features=1, min_samples_leaf=1, min_samples_split=2, bootstrap=True)
	regressor.fit(X, y)
        #print(decision_path(X))
	X_test = X[-1,:]
	y_test = []
	for i in range(int(request.get_json().get("number"))):
	    predicted1 = regressor.predict(X_test.reshape(-1,10))
	    X_test = np.append(X_test[1:],predicted1)
	    y_test.append(predicted1)

	test = scaler.inverse_transform(X[0].reshape(10,1))
	final_data = scaler.inverse_transform(y).reshape(number_of_data-10)
	tested_data = np.append(test,final_data)
	final_data = np.append(tested_data,scaler.inverse_transform(np.array(y_test)).reshape(-1,))
	print(tested_data)
	main = {"original":' '.join(map(str, tested_data)), "final":' '.join(map(str, final_data))}
	return jsonify(main)

if __name__ == '__main__':
	app.run(host='192.168.43.204',port=5051)
