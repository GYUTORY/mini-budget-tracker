// 데이터베이스 생성 및 사용자 생성
db = db.getSiblingDB('budget_tracker');

// 사용자 생성
db.createUser({
  user: 'budget_user',
  pwd: 'budget_password',
  roles: [
    { role: 'readWrite', db: 'budget_tracker' }
  ]
});

// 컬렉션 생성 및 인덱스 설정
db.createCollection('notifications');
db.notifications.createIndex({ userId: 1, createdAt: -1 });
db.notifications.createIndex({ type: 1, status: 1 });

db.createCollection('backups');
db.backups.createIndex({ userId: 1, createdAt: -1 });
db.backups.createIndex({ status: 1 });

// 초기 데이터 삽입 (필요한 경우)
db.notifications.insertOne({
  userId: 'system',
  type: 'SYSTEM',
  title: 'Welcome to Budget Tracker',
  message: 'Welcome to Budget Tracker! Start managing your finances today.',
  status: 'UNREAD',
  createdAt: new Date(),
  updatedAt: new Date()
}); 