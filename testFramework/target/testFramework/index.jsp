<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulaire</title>
</head>
<body>
    <h1>Creer un etudiant</h1>
    <form method="POST" action="api/etudiant-binding">
        <label for="etu">ETU : </label>
        <input id="etu" name="etu" type="text" placeholder="etu..." required>
        <label for="nom">Nom : </label>
        <input id="nom" name="nom" type="text" placeholder="nom..." required>
        <button type="submit">enregistrer</button>
    </form>

    <h1>Envoyer un fichier</h1>
    <form action="api/upload" method="POST" enctype="multipart/form-data">
        <label for="fichier">Choisir un fichier : </label>
        <input type="file" id="fichier" name="fichier">
        <br><br>
        <button type="submit">Envoyer</button>
    </form>
</body>
</html>