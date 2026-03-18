from cx_Freeze import setup, Executable

build_exe_options = {
    "packages": [],
    "excludes": []
}

executables = [
    Executable("binaural_gui.py")
]

setup(
    name="Binaural Solfeggio Generator",
    version="0.1",
    description="Play Solfeggio frequencies with optional binaural beats.",
    options={"install_exe": build_exe_options},
    executables=executables
)