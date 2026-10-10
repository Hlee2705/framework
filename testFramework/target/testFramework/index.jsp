<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulaire</title>
</head>
<body>
    <h1>Créer un employé</h1>
    <form method="POST" action="api/employe-binding">
        <label for="nom">Nom : </label>
        <input id="nom" name="nom" type="text" placeholder="nom..." required>
        <button type="submit">enregistrer</button>
    </form>
</body>
</html>