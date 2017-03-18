import tensorflow as tf
from tensorflow.contrib.session_bundle import exporter
import os
import numpy as np

sess = tf.Session()
tf.logging.set_verbosity(tf.logging.INFO)


rootdir = 'TestData/'
TotalImage=0
TotalLabel=-1

#Counting Number of Images and Classes
print("Counting Number of images and classes")
for subdir, dirs, files in os.walk(rootdir):
    for file in files:
        TotalImage=TotalImage+1
    TotalLabel=TotalLabel+1
print("Total Images: "+str(TotalImage))
print("Total Classes: "+str(TotalLabel))


#Getting Labels For each image in to array
print("Getting all classes in to array for future use")
LabelArray = np.ndarray(shape=(TotalLabel), dtype=np.dtype('S20'))
Label=-1 #-1 for main directory Data/
for subdir, dirs, files in os.walk(rootdir):
    for file in files:
        LabelName=subdir.split("/")[1]
        if(Label!=-1):
            LabelArray.itemset(Label,LabelName)
    Label=Label+1


Images = np.ndarray(shape=(TotalImage,40000))
Labels = np.ndarray(shape=(TotalImage,TotalLabel))

print("working on each image")
Image=0
for subdir,dir,files in os.walk(rootdir):
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

#Parameters
x = tf.placeholder(tf.float32, [None, 40000],name='x')
W = tf.Variable(tf.zeros([40000, TotalLabel]),name='W')
b = tf.Variable(tf.zeros([TotalLabel]),name='b')

#response
y = tf.nn.softmax(tf.matmul(x, W) + b,name='y')
y_ = tf.placeholder(tf.float32, [None, TotalLabel],name='y_')
tf.add_to_collection('variables',W)
tf.add_to_collection('variables',b)


cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=y, labels=y_))

train_step = tf.train.GradientDescentOptimizer(0.5).minimize(cross_entropy)


# save summaries for visualization
tf.summary.histogram('weights', W)
tf.summary.histogram('max_weight', tf.reduce_max(W))
tf.summary.histogram('bias', b)
tf.summary.scalar('cross_entropy', cross_entropy)
tf.summary.histogram('cross_hist', cross_entropy)

#merge all summaries into one op
merged=tf.summary.merge_all()

trainwriter=tf.summary.FileWriter('Output'+'/logs/train',sess.graph)

init = tf.global_variables_initializer()
sess.run(init)
print("Training")
for i in range(100):
    mask = np.random.choice([True, False], len(Images), p=[0.10, 0.90])
    trainImages = Images[mask]
    trainLabels = Labels[mask]
    summary, _ = sess.run([merged, train_step], feed_dict={x: trainImages, y_: trainLabels})
    trainwriter.add_summary(summary, i)

print("Completed Training")
# model export path
export_path = 'Output/model'
print("Saving the model")
saver = tf.train.Saver(sharded=True)
model_exporter = exporter.Exporter(saver)
model_exporter.init(
    sess.graph.as_graph_def(),
    named_graph_signatures={
        'inputs': exporter.generic_signature({'images': x}),
        'outputs': exporter.generic_signature({'scores': y})})

model_exporter.export(export_path, tf.constant(1), sess)
print("model saved")




