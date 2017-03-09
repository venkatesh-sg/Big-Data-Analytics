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

data = pandas.read_csv("winequality.csv", header=0,delimiter=';')
rnd_indices = 4000

#training data
train_x1 = np.array(list(data["fixed acidity"][:rnd_indices]))
train_x2 = np.array(list(data["volatile acidity"][:rnd_indices]))
train_x3 = np.array(list(data["citric acid"][:rnd_indices]))
train_x4 = np.array(list(data["residual sugar"][:rnd_indices]))
train_x5 = np.array(list(data["chlorides"][:rnd_indices]))
train_x6 = np.array(list(data["free sulfur dioxide"][:rnd_indices]))
train_x7 = np.array(list(data["total sulfur dioxide"][:rnd_indices]))
train_x8 = np.array(list(data["density"][:rnd_indices]))
train_x9 = np.array(list(data["pH"][:rnd_indices]))
train_x10 = np.array(list(data["sulphates"][:rnd_indices]))
train_x11 = np.array(list(data["alcohol"][:rnd_indices]))
train_y = np.array(list(data["quality"][:rnd_indices]))

#testing Data
test_x1 = np.array(list(data["fixed acidity"][rnd_indices:]))
test_x2 = np.array(list(data["volatile acidity"][rnd_indices:]))
test_x3 = np.array(list(data["citric acid"][rnd_indices:]))
test_x4 = np.array(list(data["residual sugar"][rnd_indices:]))
test_x5 = np.array(list(data["chlorides"][rnd_indices:]))
test_x6 = np.array(list(data["free sulfur dioxide"][rnd_indices:]))
test_x7 = np.array(list(data["total sulfur dioxide"][rnd_indices:]))
test_x8 = np.array(list(data["density"][rnd_indices:]))
test_x9 = np.array(list(data["pH"][rnd_indices:]))
test_x10 = np.array(list(data["sulphates"][rnd_indices:]))
test_x11 = np.array(list(data["alcohol"][rnd_indices:]))
test_y = np.array(list(data["quality"][rnd_indices:]))

init = tf.global_variables_initializer()

with tf.Session() as sess:
    sess.run(init)
    input_fn = tf.contrib.learn.io.numpy_input_fn(
        {"x1": train_x1, "x2": train_x2, "x3": train_x3, "x4": train_x4, "x5": train_x5, "x6": train_x6,
         "x7": train_x7, "x8": train_x8, "x9": train_x9, "x10": train_x10, "x11": train_x11}, train_y,
        batch_size=train_x1.size,
        num_epochs=1000)

    estimator.fit(input_fn=input_fn, steps=1000)

    training_cost = (estimator.evaluate(input_fn=input_fn,steps=1))["loss"]
    print("training cost = " ,training_cost)

    #Testing cost

    test_fn=tf.contrib.learn.io.numpy_input_fn(
        {"x1": test_x1, "x2": test_x2, "x3": test_x3, "x4": test_x4, "x5": test_x5, "x6": test_x6,
         "x7": test_x7, "x8": test_x8, "x9": test_x9, "x10": test_x10, "x11": test_x11}, test_y,
        batch_size=test_x1.size,
        num_epochs=1000)
    testing_cost=(estimator.evaluate(input_fn=test_fn,steps=1))["loss"]

    print("testing cost = ",testing_cost)

    print("Obsolute difference = ",abs(testing_cost-training_cost))


