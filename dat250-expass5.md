## DAT250: Software Technology Experiment Assignment 5

https://github.com/selabhvl/dat250public/blob/master/expassignments/expass5.md

### Installation: MongoDB database

Using MongoDB 8.0.0 Community Edition

Validating package using SHA-256 checksum

```PowerShell
>> $sigHash = (Get-Content $Env:HomePath\Downloads\mongodb-windows-x86_64-8.0.0-signed.msi.sha256 | Out-String).SubString(0,64).ToUpper();
>> $fileHash = (Get-FileHash $Env:HomePath\Downloads\mongodb-windows-x86_64-8.0.0-signed.msi).Hash.Trim();
>> echo $sigHash; echo $fileHash;
>> $sigHash -eq $fileHash;

778F03552B6638822C18A9A2E8996D31CF12E4C9B87FFC73BE8CE71E0A8465E9
778F03552B6638822C18A9A2E8996D31CF12E4C9B87FFC73BE8CE71E0A8465E9
True
```

Using MongoDB Shell
```PowerShell
>> mongosh

Current Mongosh Log ID: 66f8bcef01d764199ec73bf7
Connecting to:          mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.3.1
Using MongoDB:          8.0.0
Using Mongosh:          2.3.1
```

### Experiment 1: MongoDB CRUD operations

**Create**
```cson
data> db.users.insertOne({ name: "user 1", age: 21 })
{ acknowledged: true, insertedId: ObjectId('66f8be6f01d764199ec73bf8') }
```

**Read**
```cson
data> db.users.find({ name: "user 1" })
[
  {
    _id: ObjectId('66f8be6f01d764199ec73bf8'),
    name: 'user 1',
    age: 21
  }
]
```

**Update**
```cson
data> db.users.updateOne({ name: "user 1" }, { $set: { name: "updated user 1"}, $inc: { age: 1 }})
{ acknowledged: true, insertId: null, matchedCount: 1, modifiedCount, 1, upsertedCount: 0 }

data> db.users.find()
[
  {
    _id: ObjectId('66f8be6f01d764199ec73bf8'),
    name: 'updated user 1',
    age: 22
  }
]
```

**Delete**
```cson
data> db.users.deleteOne({ name: "updated user 1"})
{ acknowledged: true, deletedCount: 1 }
```

### Experiment 2: Aggregation

**Create dataset**
```cson
data> db.users.insertMany([
... { age: 27, gender: "male" },
... { age: 29, gender: "female" },
... { age: 22, gender: "female" },
... { age: 25, gender: "female" },
... { age: 24, gender: "male" },
... { age: 29, gender: "male" },
... { age: 26, gender: "female" },
... { age: 20, gender: "female" }
... ])
```

**Calculating average and standard deviation of age per gender and combined using mapReduce**
```cson
data> db.users.mapReduce(
...  function () { emit(this.gender, this.age); emit("combined", this.age)},
...  function (catagory, age) {
...     var mean = Array.avg(age);
...     var deviation = (Array.sum(age.map(a=>(a-mean)**2)) / (age.length - 1)) ** 0.5;
...     return { mean, deviation}; },
...  { out: "users_reduce" })
{ result: 'users_reduce', ok: 1 }
```

```cson
data> db.users_reduce.find()
[
  { _id: 'female', value: { mean: 24.4, deviation: 3.5071355833500366 } },
  { _id: 'male', value: { mean: 26.666666666666668, deviation: 2.5166114784235836 } },
  { _id: 'combined', value: { mean: 25.25, deviation: 3.1959796173138706 } }
]
```

**Calculating average and standard deviation of age per gender and combined using aggregate**
```cson
data> db.users.aggregate([
{ $facet: { 
	byGender: [{ $group: { _id: "$gender", mean: { $avg: "$age" }, deviation: { $stdDevSamp: "$age" }}}],
	combined: [{ $group: { _id: "combined", mean: { $avg: "$age" }, deviation: { $stdDevSamp: "$age" }}}],
}},
{ $project: { list: { $concatArrays: ["$byGender", "$combined"] }}},
{ $unwind: "$list" },
{ $replaceRoot: { newRoot: "$list" }},
{ $out: "users_aggregate" }
])
```
```cson
data> db.users_aggregate.find()
[
  { _id: 'male', mean: 26.666666666666668, deviation: 2.5166114784235827 },
  { _id: 'female', mean: 24.4, deviation: 3.507135583350036 },
  { _id: 'combined', mean: 25.25, deviation: 3.1959796173138706 }
]
```