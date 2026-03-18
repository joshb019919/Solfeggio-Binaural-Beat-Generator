import numpy as np
import sounddevice as sd
import tkinter as tk

fs = 44100
playing = False

# -----------------------------
# Solfeggio presets
# -----------------------------
solfeggio = [
(174,"Relieve Pain"),
(285,"Repair Your Tissues"),
(396,"Achieve Your Goals (root chakra, note C – Do)"),
(417,"Eliminate Negativity (sacral chakra, note D – Re)"),
(432,"Enhance Mental Clarity"),
(528,"Transform (solar plexus chakra, note E – Mi)"),
(639,"Improve Relationships (heart chakra, note F – Fa)"),
(741,"Awaken Your Intuition (throat chakra, note G – So)"),
(852,"Embrace Spirit (third eye chakra, note A – La)"),
(963,"Unlock Divine Consciousness (crown chakra)")
]


# -----------------------------
# Tooltip
# -----------------------------
class ToolTip:

    def __init__(self, widget, text):
        self.widget = widget
        self.text = text
        self.tip = None
        widget.bind("<Enter>", self.show)
        widget.bind("<Leave>", self.hide)

    def show(self, event=None):
        x = self.widget.winfo_rootx()+20
        y = self.widget.winfo_rooty()+20
        self.tip = tk.Toplevel(self.widget)
        self.tip.overrideredirect(True)
        self.tip.geometry(f"+{x}+{y}")
        label = tk.Label(self.tip,text=self.text,background="lightyellow")
        label.pack()

    def hide(self,event=None):
        if self.tip:
            self.tip.destroy()
            self.tip=None


def generate_audio():
    global playing

    binaural = binaural_slider.get()
    base_freq = float(freq_entry.get())
    duration = float(duration_entry.get())
    swap = swap_var.get()

    f_left = base_freq
    f_right1 = f_left + binaural

    t = np.linspace(0, duration, int(fs * duration), False)

    left = np.sin(2*np.pi*f_left*t)
    right = np.sin(2*np.pi*f_right1*t)

    if swap:
        left, right = right, left

    audio = np.column_stack((left, right))
    audio /= np.max(np.abs(audio))

    playing = True
    sd.play(audio, fs)

def stop_audio():
    global playing
    playing = False
    sd.stop()

def set_preset(value):
    binaural_slider.set(value)

# GUI window
root = tk.Tk()
root.title("Binaural Beat Generator")

# Solfeggio frequency
tk.Label(root, text="Base Frequency").grid(row=0, column=0)
freq_entry = tk.Entry(root)
freq_entry.insert(0, "432")
freq_entry.grid(row=0, column=1)

# Duration
tk.Label(root, text="Duration (seconds)").grid(row=1, column=0)
duration_entry = tk.Entry(root)
duration_entry.insert(0, "5")
duration_entry.grid(row=1, column=1)

# Binaural beat slider
tk.Label(root, text="Binaural Beat (Hz)").grid(row=2, column=0)
binaural_slider = tk.Scale(root, from_=0.5, to=40, resolution=0.5,
                           orient=tk.HORIZONTAL, length=250)
binaural_slider.set(4)
binaural_slider.grid(row=2, column=1)

# Swap ears
swap_var = tk.BooleanVar()
tk.Checkbutton(root, text="Swap Ears", variable=swap_var).grid(row=3, columnspan=2)

# solfeggio buttons
solfeggio_frame = tk.Frame(root)
solfeggio_frame.grid(row=4, columnspan=2, pady=10)

for f,desc in solfeggio:

    b=tk.Button(solfeggio_frame,text=str(f),
    command=lambda v=f:freq_entry.delete(0,tk.END) or freq_entry.insert(0, str(v)))

    b.pack(side="left")
    ToolTip(b,desc)

# Preset buttons
preset_frame = tk.Frame(root)
preset_frame.grid(row=5, columnspan=2, pady=5)

tk.Button(preset_frame, text="Delta (2Hz)", command=lambda: set_preset(2)).pack(side=tk.LEFT)
tk.Button(preset_frame, text="Theta (6Hz)", command=lambda: set_preset(6)).pack(side=tk.LEFT)
tk.Button(preset_frame, text="Alpha (10Hz)", command=lambda: set_preset(10)).pack(side=tk.LEFT)
tk.Button(preset_frame, text="Beta (20Hz)", command=lambda: set_preset(20)).pack(side=tk.LEFT)
tk.Button(preset_frame, text="Gamma (40Hz)", command=lambda: set_preset(40)).pack(side=tk.LEFT)

# Start / Stop buttons
control_frame = tk.Frame(root)
control_frame.grid(row=6, columnspan=2, pady=10)

tk.Button(control_frame, text="Start", width=10, command=generate_audio).pack(side=tk.LEFT, padx=5)
tk.Button(control_frame, text="Stop", width=10, command=stop_audio).pack(side=tk.LEFT, padx=5)

root.mainloop()
