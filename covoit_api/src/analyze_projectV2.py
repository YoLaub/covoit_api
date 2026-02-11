import os
import google.generativeai as genai
from colorama import init, Fore, Style # Importation pour la gestion des couleurs

# Initialiser colorama pour que les couleurs fonctionnent sur différents terminaux
init(autoreset=True)

# --- CONFIGURATION ---
# REMPLACER CECI avec votre clé AI Studio (ou la charger depuis une variable d'environnement)
API_KEY = "AIzaSyBS6PhZmVP2mzcl_NWZhB1FJD4sooU8NAA" 
TARGET_FOLDER = "."
# Dossiers et fichiers à ignorer (très important pour économiser des tokens)
IGNORE_DIRS = {'.git', '__pycache__', 'node_modules', 'venv', 'env', '.idea', '.vscode', 'build', 'dist'}
IGNORE_EXT = {'.png', '.jpg', '.jpeg', '.gif', '.ico', '.pdf', '.exe', '.bin', '.lock', '.pyc'}

# Modèles disponibles avec description (Source des modèles :)
MODELS = {
    1: {"name": "gemini-2.5-flash", "desc": "Modèle rapide et polyvalent, bon pour les tâches à faible latence et les analyses de grande échelle."},
    2: {"name": "gemini-3-flash-preview", "desc": "Modèle preview et costaud et rapide."},
    3: {"name": "gemini-3-pro-preview", "desc": "Modèle preview et costaud ."},
}

def get_project_context(root_dir):
    """Parcourt le répertoire, filtre les fichiers/dossiers ignorés et concatène le contenu."""
    project_content = Fore.GREEN + Style.BRIGHT + "Voici l'intégralité du code source du projet. Analyse-le pour répondre aux questions.\n\n" + Style.RESET_ALL
    file_count = 0
    
    print(Fore.YELLOW + "⏳ Lecture des fichiers en cours...", end="", flush=True)
    
    for dirpath, dirnames, filenames in os.walk(root_dir):
        # Filtrer les dossiers ignorés
        dirnames[:] = [d for d in dirnames if d not in IGNORE_DIRS]
        
        for filename in filenames:
            ext = os.path.splitext(filename)[1].lower()
            if ext in IGNORE_EXT:
                continue
                
            filepath = os.path.join(dirpath, filename)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                    # Formater clairement pour Gemini : Chemin du fichier + Contenu
                    project_content += f"--- FICHIER: {filepath} ---\n{content}\n\n"
                    file_count += 1
                    if file_count % 10 == 0: print(Fore.YELLOW + ".", end="", flush=True)
            except Exception:
                # Ignorer les fichiers binaires ou illisibles
                continue

    print(f"\n✅ {Fore.GREEN}{file_count} fichiers chargés dans le contexte." + Style.RESET_ALL)
    return project_content

def choose_model():
    """Propose le choix du modèle à l'utilisateur."""
    while True:
        print(Fore.CYAN + "\n--- SÉLECTION DU MODÈLE GEMINI ---")
        for key, model in MODELS.items():
            print(f"{Fore.CYAN}[{key}]{Fore.RESET} {Fore.WHITE}{model['name']}{Fore.RESET}: {model['desc']}")
        
        choice = input(f"{Fore.YELLOW}Entrez le numéro du modèle à utiliser (1-{len(MODELS)}, défaut 1): {Fore.RESET}")
        
        if not choice:
            choice = '1' # Choix par défaut
        
        try:
            choice_num = int(choice)
            if choice_num in MODELS:
                selected_model_name = MODELS[choice_num]["name"]
                print(f"{Fore.GREEN}Modèle sélectionné : {selected_model_name}{Style.RESET_ALL}")
                return selected_model_name
            else:
                print(Fore.RED + "Choix invalide. Veuillez entrer 1, 2 ou 3." + Style.RESET_ALL)
        except ValueError:
            print(Fore.RED + "Saisie invalide. Veuillez entrer un numéro." + Style.RESET_ALL)

def colorize_code_block(text):
    """
    Détecte les blocs de code Markdown (```...) et les colore en jaune vif (Fore.YELLOW).
    Le reste du texte reste blanc.
    """
    parts = text.split('```')
    colored_text = []
    
    for i, part in enumerate(parts):
        if i % 2 == 1:
            # Bloc de code (impair)
            colored_text.append(Fore.YELLOW + '```' + part + '```' + Fore.WHITE)
        else:
            # Texte normal (pair)
            colored_text.append(part)
            
    return "".join(colored_text)

def main():
    # 1. Sélection du modèle
    selected_model = choose_model()
    
    # 2. Configuration et chargement du contexte
    if not API_KEY :
        print(Fore.RED + Style.BRIGHT + "\nERREUR: Veuillez remplacer la valeur de la variable API_KEY dans le script." + Style.RESET_ALL)
        return
        
    genai.configure(api_key=API_KEY)
    
    context = get_project_context(TARGET_FOLDER)
    
    # 3. Configuration et démarrage du Chat
    # On utilise le modèle sélectionné
    model = genai.GenerativeModel(
        model_name=selected_model,
        system_instruction="Tu es un Architecte Logiciel Senior. Tu as accès à tout le code source ci-dessous. Tes réponses doivent être techniques, précises et citer les fichiers concernés."
    )
    
    chat = model.start_chat(history=[
        {"role": "user", "parts": [context]}
    ])
    
    print(Fore.BLUE + Style.BRIGHT + "\n--- GEMINI PROJECT ANALYZER ACTIF ---")
    print(Fore.BLUE + f"Modèle utilisé: {selected_model}")
    print(Fore.BLUE + "Le contexte du projet est prêt. Posez vos questions (ex: 'Explique l'architecture', 'Y a-t-il des bugs ?')\n" + Style.RESET_ALL)

    # 4. Boucle de Chat
    while True:
        try:
            user_input = input(Fore.GREEN + "\nVous: " + Fore.RESET)
            if user_input.lower() in ['exit', 'quit']:
                break
            
            print(Fore.CYAN + "\nGemini: " + Fore.WHITE, end="")
            
            # Utilisation de la fonction streaming (si elle est supportée) ou envoi simple
            response = chat.send_message(user_input)
            
            # Colorisation des blocs de code
            colored_response = colorize_code_block(response.text)
            print(colored_response)
            
        except Exception as e:
            print(Fore.RED + f"\nErreur : {e}" + Style.RESET_ALL)
            break # Sortir en cas d'erreur API

if __name__ == "__main__":
    main()