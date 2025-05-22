package top.cyclops.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

typealias ViewBindingCreator<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB