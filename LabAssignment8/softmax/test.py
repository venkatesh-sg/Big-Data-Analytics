import tensorflow as tf
import os
import numpy as np
tf.logging.set_verbosity(tf.logging.DEBUG)
sess = tf.Session()

print("Importing saved model")
new_saver = tf.train.import_meta_graph('Output/model/00000001/export.meta')
new_saver.restore(sess, 'Output/model/00000001/export')

for v in tf.get_collection('variables'):
    print(v.name)
print(sess.run(tf.global_variables()))



print("Getting labels from training directory")
Traindir="Data/"
TotalLabel=-1

#Counting Number of Classes
print("Counting Number of Trained classes")
for subdir, dirs, files in os.walk(Traindir):
    TotalLabel=TotalLabel+1

LabelArray = np.ndarray(shape=(), dtype=np.dtype('S20'))
Label=-1 #-1 for main directory TestData/
for subdir, dirs, files in os.walk(Traindir):
    for file in files:
        LabelName=subdir.split("/")[1]
        if(Label!=-1):
            LabelArray.itemset(Label,LabelName)
    Label=Label+1


print("counting number of images for testing")
Testdir="TestData/"
TotalImage=0
for subdir, dirs, files in os.walk(Testdir):
    for file in files:
        TotalImage=TotalImage+1

Images = np.ndarray(shape=(TotalImage,40000))
Labels = np.ndarray(shape=(TotalImage,TotalLabel))


print("working on each image")
Image=0
for subdir,dir,files in os.walk(Testdir):
    for file in files:
        curFile = file
        curLabel = subdir.split("/")[1]


        #training label matrix
        index=np.where(LabelArray==str.encode(curLabel))
        fileLabels = np.zeros(shape=(TotalLabel))
        fileLabels[index[0]] = 1
        Labels[Image] = fileLabels

        file_contents = tf.read_file(os.path.join(subdir, file))
        Imagecontent = tf.image.decode_jpeg(file_contents, channels=1)
        image = tf.image.resize_images(Imagecontent, [200, 200])

        with sess.as_default():
            flatImage = image.eval().ravel()
        flatImage = np.multiply(flatImage, 1.0 / 255.0)

        Images[Image]=flatImage


# get saved weights
W = tf.get_collection('variables')[0]
b = tf.get_collection('variables')[1]

# placeholders for test images and labels
x = tf.placeholder(tf.float32, [None, 40000],name='x')
y_ = tf.placeholder(tf.float32, [None, TotalLabel],name='y_')

y = tf.nn.softmax(tf.matmul(x, W) + b,name='y')

# compare predicted label and actual label
correct_prediction = tf.equal(tf.argmax(y,1), tf.argmax(y_,1))

# accuracy op
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

accu=sess.run(accuracy, feed_dict={x: Images, y_: Labels})
print("Accuracy:"+accu)
