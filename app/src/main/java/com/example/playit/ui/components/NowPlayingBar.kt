package com.example.playit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.playit.R
import com.example.playit.viewmodel.MediaPlayerViewModel

@Composable
fun NowPlayingBar(
    viewModel: MediaPlayerViewModel,
    onPlayerClick: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()

    // Update progress every second if the song is playing


    currentSong?.let { song ->
        val isLightMode = MaterialTheme.colorScheme.background.luminance() > 0.5f
        val surfaceColor = if (isLightMode) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onPlayerClick),
            color = surfaceColor,
            tonalElevation = 0.dp,
            shadowElevation = if (isLightMode) 2.dp else 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(surfaceColor)
            ) {
                // Main content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Album Art with subtle scale animation
                    val scale by animateFloatAsState(
                        targetValue = if (isPlaying) 1.1f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = song.artwork,
                                error = painterResource(R.drawable.default_album_art)
                            ),
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(scale),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Song Info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Play/Pause Button with animation
                    val buttonScale by animateFloatAsState(
                        targetValue = if (isPlaying) 1.1f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    IconButton(
                        onClick = { viewModel.togglePlayPause() },
                        modifier = Modifier
                            .size(44.dp)
                            .scale(buttonScale)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isPlaying) R.drawable.ic_pause
                                else R.drawable.ic_play
                            ),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Animated progress bar
                val progressAnimation by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )

                LinearProgressIndicator(
                    progress = progressAnimation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
