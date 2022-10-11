
import tkinter
from tkinter import *
import tkinter as tk



class ErrorMessage:
    def __init__(self, message=''):
        frame = tk.Tk()
        frame.title("Error")
        frame.minsize(height=150, width=225)

        message = Label(frame, text=message)
        message.pack(expand=YES)
        frame.mainloop()
