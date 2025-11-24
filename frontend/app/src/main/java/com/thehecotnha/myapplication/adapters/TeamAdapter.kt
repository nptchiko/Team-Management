package com.thehecotnha.myapplication.activities.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.databinding.ItemUserTeamBinding
import com.thehecotnha.myapplication.models.TeamItem
import com.thehecotnha.myapplication.models.TeamMember
import com.thehecotnha.myapplication.utils.enums.TeamRole

class TeamAdapter(
    private val teamMembers: List<TeamItem>,
    private val onDeleteClick: (TeamItem) ->Unit
    ) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(val binding: ItemUserTeamBinding) : RecyclerView.ViewHolder(binding.root) {
       fun bind(member: TeamItem) {
            binding.userName.text = member.name
            binding.userRole.text = when(member.role){
                TeamRole.MEMBER.name -> "MEMBER"
                TeamRole.ADMIN.name -> "ADMIN"
                "Administrator" -> "ADMIN"
                else -> "Member"
            }
            binding.ivDelete.setOnClickListener {
                onDeleteClick(member)
            }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemUserTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val member = teamMembers[position]
        holder.bind(member)
    }

    override fun getItemCount() = teamMembers.size
}