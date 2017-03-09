import numpy as np
import tensorflow as tf
import pandas

features = [tf.contrib.layers.real_valued_column("x1", dimension=1),tf.contrib.layers.real_valued_column("x2", dimension=1),
            tf.contrib.layers.real_valued_column("x3", dimension=1),tf.contrib.layers.real_valued_column("x4", dimension=1),
            tf.contrib.layers.real_valued_column("x5", dimension=1),tf.contrib.layers.real_valued_column("x6", dimension=1),
            tf.contrib.layers.real_valued_column("x7", dimension=1),tf.contrib.layers.real_valued_column("x8", dimension=1),
            tf.contrib.layers.real_valued_column("x9", dimension=1),tf.contrib.layers.real_valued_column("x10", dimension=1),
            tf.contrib.layers.real_valued_column("x11", dimension=1)]

estimator = tf.contrib.learn.LinearRegressor(feature_columns=features)

init = tf.global_variables_initializer()
k=10

with tf.Session() as sess:
    sess.run(init)
    data = pandas.read_csv("winequality.csv", header=0, delimiter=';')
    part=int(data.shape[0]/k)
    TtrainingCost=0
    TtestingCost=0
    for i in range(k):
        ind=i*part
        nextind=(i+1)*part

        #9 parts of training data excluding testing part
        train_x1 = np.concatenate((data["fixed acidity"][:ind],data["fixed acidity"][nextind:]),0)
        train_x2 = np.concatenate((data["volatile acidity"][:ind],data["volatile acidity"][nextind:]),0)
        train_x3 = np.concatenate((data["citric acid"][0:ind],data["citric acid"][nextind:]),0)
        train_x4 = np.concatenate((data["residual sugar"][0:ind],data["residual sugar"][nextind:]),0)
        train_x5 = np.concatenate((data["chlorides"][0:ind],data["chlorides"][nextind:]),0)
        train_x6 = np.concatenate((data["free sulfur dioxide"][0:ind],data["free sulfur dioxide"][nextind:]),0)
        train_x7 = np.concatenate((data["total sulfur dioxide"][0:ind],data["total sulfur dioxide"][nextind:]),0)
        train_x8 = np.concatenate((data["density"][0:ind],data["density"][nextind:]),0)
        train_x9 = np.concatenate((data["pH"][0:ind],data["pH"][nextind:]),0)
        train_x10 = np.concatenate((data["sulphates"][0:ind],data["sulphates"][nextind:]),0)
        train_x11 = np.concatenate((data["alcohol"][0:ind],data["alcohol"][nextind:]),0)
        train_y = np.concatenate((data["quality"][0:ind],data["quality"][nextind:]),0)

        #testing part
        test_x1 = np.array(list(data["fixed acidity"][ind:nextind]))
        test_x2 = np.array(list(data["volatile acidity"][ind:nextind]))
        test_x3 = np.array(list(data["citric acid"][ind:nextind]))
        test_x4 = np.array(list(data["residual sugar"][ind:nextind]))
        test_x5 = np.array(list(data["chlorides"][ind:nextind]))
        test_x6 = np.array(list(data["free sulfur dioxide"][ind:nextind]))
        test_x7 = np.array(list(data["total sulfur dioxide"][ind:nextind]))
        test_x8 = np.array(list(data["density"][ind:nextind]))
        test_x9 = np.array(list(data["pH"][ind:nextind]))
        test_x10 = np.array(list(data["sulphates"][ind:nextind]))
        test_x11 = np.array(list(data["alcohol"][ind:nextind]))
        test_y = np.array(list(data["quality"][ind:nextind]))

        input_fn = tf.contrib.learn.io.numpy_input_fn(
            {"x1": train_x1, "x2": train_x2, "x3": train_x3, "x4": train_x4, "x5": train_x5, "x6": train_x6,
             "x7": train_x7, "x8": train_x8, "x9": train_x9, "x10": train_x10, "x11": train_x11}, train_y,
            batch_size=train_x1.size,
            num_epochs=100)
        estimator.fit(input_fn=input_fn, steps=100)
        training_cost = (estimator.evaluate(input_fn=input_fn, steps=1))["loss"]
        print("training cost excluding ",i+1," part = ", training_cost)
        TtrainingCost=TtrainingCost+training_cost

        # Testing cost
        test_fn = tf.contrib.learn.io.numpy_input_fn(
            {"x1": test_x1, "x2": test_x2, "x3": test_x3, "x4": test_x4, "x5": test_x5, "x6": test_x6,
             "x7": test_x7, "x8": test_x8, "x9": test_x9, "x10": test_x10, "x11": test_x11}, test_y,
            batch_size=test_x1.size,
            num_epochs=100)
        testing_cost = (estimator.evaluate(input_fn=test_fn, steps=1))["loss"]
        print("testing cost for ",i+1," part = ", testing_cost)
        TtestingCost=TtestingCost+testing_cost

    print("Mean Trraining Error = ",TtrainingCost/k)
    print("Mean Testing Error = ",TtestingCost/k)