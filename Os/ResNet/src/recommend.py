import pymysql
import torch
from torchvision import models, transforms
from PIL import Image
import os
import pickle
import numpy as np
from scipy.spatial import distance
from flask import Flask, request, jsonify

# 数据库连接配置
HOST = 'localhost'
PORT = 3306
USER = 'root'
PASSWORD = 'Ly030704'
DATABASE = 'seckill'

# 连接到MySQL数据库
connection = pymysql.connect(host=HOST, port=PORT, user=USER, password=PASSWORD, db=DATABASE)

# 基础图片路径
BASE_IMAGE_PATH = 'D:/desktop/os/Os/Os'

# 预训练模型和预处理
model = models.resnet50(pretrained=True)
model.eval()
preprocess = transforms.Compose([
    transforms.Resize(256),
    transforms.CenterCrop(224),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

def extract_features(image_path):
    img = Image.open(image_path).convert('RGB')
    img_t = preprocess(img)
    batch_t = torch.unsqueeze(img_t, 0)
    with torch.no_grad():
        features = model(batch_t)
    return features.numpy().flatten()

def read_products():
    with connection.cursor() as cursor:
        sql = "SELECT id, imgs FROM good"
        cursor.execute(sql)
        results = cursor.fetchall()
        for product_id, image_path in results:
            # full_image_path = os.path.join(BASE_IMAGE_PATH, image_path)
            full_image_path=BASE_IMAGE_PATH+image_path
            # print(full_image_path)
            if os.path.exists(full_image_path):
                features = extract_features(full_image_path)
                yield (product_id, features)

class FeatureDB:
    def __init__(self):
        self.db = {}

    def add_feature(self, product_id, features):
        self.db[product_id] = features

    def find_similar_products(self, product_id, top_k=5):
        query_features = self.db.get(product_id)
        if query_features is None:
            return []
        distances = [(pid, distance.cosine(query_features, features))
                     for pid, features in self.db.items() if pid != product_id]
        distances.sort(key=lambda x: x[1])
        return [pid for pid, dist in distances[:top_k]]

db = FeatureDB()  # 创建FeatureDB实例

# 加载特征到db实例
def load_features_to_db():
    try:
        with open('image_features.pkl', 'rb') as f:
            features = pickle.load(f)
            for product_id, feats in features.items():
                db.add_feature(product_id, feats)
    except FileNotFoundError:
        pass



import pickle

def save_all_features():
    features_dict = {}
    product_count = 0  # 添加计数器来跟踪处理了多少产品
    for product_id, features in read_products():
        features_dict[product_id] = features
        product_count += 1
        # print(f"Processed product {product_id}, Total products processed: {product_count}")
    with open('image_features.pkl', 'wb') as f:
        pickle.dump(features_dict, f)
    print(f"All features saved to 'image_features.pkl'. Total products: {product_count}")





load_features_to_db()

app = Flask(__name__)

@app.route('/get_similar_products', methods=['GET'])
def get_similar_products():
    product_id = request.args.get('product_id', default=1, type=int)
    top_k = request.args.get('top_k', default=5, type=int)
    similar_products = db.find_similar_products(product_id, top_k)
    print(similar_products)
    return jsonify(similar_products)

@app.route('/update', methods=['GET'])
def update_features():
    try:
        save_all_features()  # 调用前面定义的保存所有特征的函数
        return jsonify({'status': 'success', 'message': 'Features updated successfully'}), 200
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

if __name__ == '__main__':
    save_all_features()
    app.run(debug=True, host='0.0.0.0')
