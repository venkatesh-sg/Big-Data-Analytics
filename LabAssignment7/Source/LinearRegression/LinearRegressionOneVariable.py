
from __future__ import print_function
import tensorflow as tf
import numpy as np
import pandas
import matplotlib.pyplot as plt

rnd = np.random


data = pandas.read_csv("winequality.csv", header=0,delimiter=';')
rnd_indices = 4000
train_x = np.array(list(data["residual sugar"][:rnd_indices]))
train_y = np.array(list(data["quality"][:rnd_indices]))

test_x = np.array(list(data["residual sugar"][rnd_indices:]))
test_y = np.array(list(data["quality"][rnd_indices:]))


X = tf.placeholder("float")

Y = tf.placeholder("float")

# create a shared variable for the weight matrix
w = tf.Variable(rnd.randn(), name="weights")
b = tf.Variable(rnd.randn(), name="bias")

# prediction function
y_model = tf.add(tf.multiply(X, w), b)

# squared error
cost = tf.reduce_sum(tf.square(y_model-Y))/ (2 * train_x.shape[0])

# construct an optimizer to minimize cost and fit line to my data
train_op = tf.train.GradientDescentOptimizer(0.001).minimize(cost)

# Launch the graph in a session
sess = tf.Session()
# Initializing the variables
init = tf.global_variables_initializer()
# you need to initialize variables
sess.run(init)
print(sess.run(w),sess.run(b))

for i in range(1000):
    sess.run(train_op,{X: train_x, Y: train_y})

print("Optimization Finished!")
training_cost = sess.run(cost, feed_dict={X: train_x, Y: train_y})

print("Training cost=", training_cost, "W=", sess.run(w), "b=", sess.run(b), '\n')
testing_cost = sess.run(
    tf.reduce_sum(tf.square(y_model - Y)),
    feed_dict={X: test_x, Y: test_y})
print("Testing cost=", testing_cost)
print("Absolute square loss difference:", abs(
    training_cost - testing_cost))

plt.plot(train_x, train_y, 'ro', label='Original data')
plt.plot(train_x, sess.run(w) * train_x + sess.run(b), label='Fitted line')
plt.plot(test_x,test_y,'ro',c='g',label='Test data')
plt.plot(test_x,sess.run(w) * test_x + sess.run(b),label='Fitted line')
plt.legend()
plt.show()
