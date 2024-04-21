# -*- coding: utf-8 -*-

from __future__ import print_function

import numpy as np
import pandas as pd
import os

DB_dir = 'database'
DB_csv = 'data.csv'



class Database(object):

  def __init__(self):
    self._gen_csv()
    self.data = pd.read_csv(DB_csv)
    self.classes = set(self.data["cls"])

  def _gen_csv(self):
    if os.path.exists(DB_csv):
      return
    with open(DB_csv, 'w', encoding='UTF-8') as f:
      f.write("img,cls")
      for root, _, files in os.walk(DB_dir, topdown=False):
        cls = root.split('/')[-1]
        for name in files:
          if not name.endswith('.jpg'):
            continue
          img = os.path.join(root, name)
          f.write("\n{},{}".format(img, cls))

  def __len__(self):
    return len(self.data)

  def get_class(self):
    return self.classes

  def get_data(self):
    return self.data


if __name__ == "__main__":
  db = Database()
  data = db.get_data()
  classes = db.get_class()

  print("DB length:", len(db))
  print(classes)
import pickle
import os
import torch
import numpy as np

class ImageDatabase:
    def __init__(self, db_path="image_features.db"):
        self.db_path = db_path
        self.features = {}
        self.load_db()

    def add_image(self, image_path, feature_vector):
        """ 添加新图像的特征到数据库 """
        self.features[image_path] = feature_vector
        self.save_db()

    def save_db(self):
        """ 保存特征数据库到磁盘 """
        with open(self.db_path, 'wb') as f:
            pickle.dump(self.features, f)

    def load_db(self):
        """ 从磁盘加载特征数据库 """
        if os.path.exists(self.db_path):
            with open(self.db_path, 'rb') as f:
                self.features = pickle.load(f)
        else:
            self.features = {}

    def find_similar(self, query_feature):
        """ 找到最相似的图像特征 """
        max_similarity = -1
        similar_image_path = None
        for path, feature in self.features.items():
            similarity = self.cosine_similarity(query_feature, feature)
            if similarity > max_similarity:
                max_similarity = similarity
                similar_image_path = path
        return similar_image_path

    @staticmethod
    def cosine_similarity(a, b):
        """ 计算两个特征向量之间的余弦相似度 """
        a = a.flatten()
        b = b.flatten()
        return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))

